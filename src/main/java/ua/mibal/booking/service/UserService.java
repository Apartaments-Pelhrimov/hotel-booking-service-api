/*
 * Copyright (c) 2023. Mykhailo Balakhon mailto:9mohapx9@gmail.com
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

import lombok.RequiredArgsConstructor;
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
import ua.mibal.booking.model.exception.IllegalPasswordException;
import ua.mibal.booking.model.exception.entity.UserNotFoundException;
import ua.mibal.booking.model.mapper.UserMapper;
import ua.mibal.booking.repository.UserRepository;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserDto getOneDto(String email) {
        return userMapper.toDto(getOne(email));
    }

    public User getOne(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    public boolean isExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void delete(DeleteMeDto deleteMeDto, String email) {
        String password = deleteMeDto.password();
        validatePassword(password, email);
        userRepository.deleteByEmail(email);
    }

    public User save(RegistrationDto registrationDto, String password) {
        User user = userMapper.toEntity(registrationDto, password);
        return userRepository.save(user);
    }

    public void changePassword(ChangePasswordDto changePasswordDto,
                               String email) {
        String oldPassword = changePasswordDto.oldPassword();
        validatePassword(oldPassword, email);
        String newEncodedPassword = passwordEncoder.encode(changePasswordDto.newPassword());
        userRepository.updateUserPasswordByEmail(newEncodedPassword, email);
    }

    @Transactional
    public void changeDetails(ChangeUserDetailsDto changeUserDetailsDto,
                              String email) {
        User user = getOne(email);
        userMapper.update(user, changeUserDetailsDto);
    }

    @Transactional
    public void changeNotificationSettings(ChangeNotificationSettingsDto changeNotificationSettingsDto,
                                           String email) {
        User user = getOne(email);
        userMapper.update(user.getNotificationSettings(), changeNotificationSettingsDto);
    }

    private void validatePassword(String password, String email) {
        String encodedPassword = userRepository.findPasswordByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        if (!passwordEncoder.matches(password, encodedPassword)) {
            throw new IllegalPasswordException();
        }
    }
}
