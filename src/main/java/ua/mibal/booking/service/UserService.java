/*
 * Copyright (c) 2023. Mykhailo Balakhon, mailto:9mohapx9@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ua.mibal.booking.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.mibal.booking.model.dto.auth.RegistrationDto;
import ua.mibal.booking.model.dto.request.ChangeNotificationSettingsDto;
import ua.mibal.booking.model.dto.request.ChangePasswordDto;
import ua.mibal.booking.model.dto.request.ChangeUserDetailsDto;
import ua.mibal.booking.model.dto.request.DeleteMeDto;
import ua.mibal.booking.model.dto.response.UserDto;
import ua.mibal.booking.model.entity.User;
import ua.mibal.booking.model.entity.embeddable.Phone;
import ua.mibal.booking.model.exception.IllegalPasswordException;
import ua.mibal.booking.model.mapper.UserMapper;
import ua.mibal.booking.repository.UserRepository;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Map.of;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserDto getOneByAuthentication(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Entity User by email=" + email + " not found"));
    }

    public User getOneByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Entity User by email=" + email + " not found"));
    }

    public boolean isExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void deleteByAuthentication(DeleteMeDto deleteMeDto, Authentication authentication) {
        String password = deleteMeDto.password();
        String email = authentication.getName();
        validatePassword(password, email);
        userRepository.deleteByEmail(email);
    }

    public User save(RegistrationDto registrationDto, String password) {
        User user = userMapper.toEntity(registrationDto, password);
        return userRepository.save(user);
    }

    public void changePasswordByAuthentication(ChangePasswordDto changePasswordDto,
                                               Authentication authentication) {
        String oldPassword = changePasswordDto.oldPassword();
        String email = authentication.getName();
        validatePassword(oldPassword, email);
        String newEncodedPassword = passwordEncoder.encode(changePasswordDto.newPassword());
        userRepository.updateUserPasswordByEmail(newEncodedPassword, email);
    }

    private void validatePassword(String password, String email) {
        String encodedPassword = userRepository.findPasswordByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Entity User by email=" + email + " not found"));
        if (!passwordEncoder.matches(password, encodedPassword)) {
            throw new IllegalPasswordException();
        }
    }

    @Transactional
    public void changeDetails(ChangeUserDetailsDto changeUserDetailsDto,
                              Authentication authentication) {
        User user = getOneByEmail(authentication.getName());
        performIfNotNull(of(
                (Supplier<String>) changeUserDetailsDto::firstName, user::setFirstName,
                changeUserDetailsDto::lastName, user::setLastName,
                changeUserDetailsDto::phone, phone -> user.setPhone(new Phone(phone))
        ));
    }

    @Transactional
    public void changeNotificationSettingsByAuthentication(ChangeNotificationSettingsDto changeNotificationSettingsDto,
                                                           Authentication authentication) {
        User user = getOneByEmail(authentication.getName());
        performIfNotNull(of(
                (Supplier<Boolean>) changeNotificationSettingsDto::receiveOrderEmails, user.getNotificationSettings()::setReceiveOrderEmails,
                changeNotificationSettingsDto::receiveNewsEmails, user.getNotificationSettings()::setReceiveNewsEmails
        ));
    }

    private <T> void performIfNotNull(Map<Supplier<T>, Consumer<T>> fieldsMap) {
        fieldsMap.forEach((sup, con) ->
                Optional.ofNullable(sup.get()).ifPresent(con)
        );
    }
}
