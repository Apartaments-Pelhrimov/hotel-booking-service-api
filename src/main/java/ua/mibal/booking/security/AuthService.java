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

package ua.mibal.booking.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.mibal.booking.mapper.UserMapper;
import ua.mibal.booking.model.dto.AuthResponseDto;
import ua.mibal.booking.model.dto.RegistrationDto;
import ua.mibal.booking.model.entity.User;
import ua.mibal.booking.repository.UserRepository;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@RequiredArgsConstructor
@Service
public class AuthService {
    private final TokenService tokenService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponseDto getUserToken(Authentication authentication) {
        return userMapper.toAuthResponse(
                (User) authentication.getPrincipal(),
                tokenService.generateToken(authentication)
        );
    }

    public AuthResponseDto register(RegistrationDto registrationDto) {
        String encodedPass = passwordEncoder.encode(registrationDto.password());
        User user = userMapper.toEntity(registrationDto, encodedPass);
        userRepository.save(user);
        String token = tokenService.generateToken(user);
        return userMapper.toAuthResponse(user, token);
    }
}
