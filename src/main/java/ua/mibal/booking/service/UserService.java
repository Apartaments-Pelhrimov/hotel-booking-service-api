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
import ua.mibal.booking.model.dto.auth.RegistrationDto;
import ua.mibal.booking.model.dto.request.DeleteMeDto;
import ua.mibal.booking.model.dto.response.UserDto;
import ua.mibal.booking.model.entity.User;
import ua.mibal.booking.model.exception.IllegalPasswordException;
import ua.mibal.booking.model.mapper.UserMapper;
import ua.mibal.booking.repository.UserRepository;

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
        return userRepository.findByEmailFetchHotels(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Entity User by email=" + email + " not found"));
    }

    public boolean isExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void deleteMe(DeleteMeDto deleteMeDto, Authentication authentication) {
        String password = deleteMeDto.password();
        String email = authentication.getName();
        String dbPassword = userRepository.findPasswordByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Entity User by email=" + email + " not found"));
        validatePassword(password, dbPassword);
        userRepository.deleteByEmail(email);
    }

    private void validatePassword(String actual, String expected) {
        if (!passwordEncoder.matches(actual, expected)) {
            throw new IllegalPasswordException();
        }
    }

    public User save(RegistrationDto registrationDto, String password) {
        User user = userMapper.toEntity(registrationDto, password);
        return userRepository.save(user);
    }
}
