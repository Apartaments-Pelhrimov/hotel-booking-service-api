/*
 * Copyright (c) 2024. Mykhailo Balakhon mailto:9mohapx9@gmail.com
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

package ua.mibal.booking.service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.mibal.booking.model.dto.LoginDto;
import ua.mibal.booking.model.dto.auth.RegistrationDto;
import ua.mibal.booking.model.dto.auth.TokenDto;
import ua.mibal.booking.model.entity.Token;
import ua.mibal.booking.model.entity.User;
import ua.mibal.booking.model.exception.EmailAlreadyExistsException;
import ua.mibal.booking.model.exception.IllegalPasswordException;
import ua.mibal.booking.model.exception.entity.UserNotFoundException;
import ua.mibal.booking.model.exception.marker.NotAuthorizedException;
import ua.mibal.booking.model.mapper.UserMapper;
import ua.mibal.booking.service.UserService;
import ua.mibal.booking.service.security.jwt.JwtTokenService;
import ua.mibal.email.api.EmailSendingService;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class AuthService {
    private final JwtTokenService jwtTokenService;
    private final UserMapper userMapper;
    private final UserService userService;
    private final TokenService tokenService;
    private final EmailSendingService emailSendingService;
    private final PasswordEncoder passwordEncoder;

    public TokenDto login(LoginDto login) {
        try {
            return loginByCredentials(login);
        } catch (UserNotFoundException | IllegalPasswordException hidden) {
            // To hide from a client what is incorrect: username or password
            throw new NotAuthorizedException();
        }
    }

    @Transactional
    public void register(RegistrationDto registrationDto) {
        validateEmailDoesNotExist(registrationDto.email());
        User user = userService.save(registrationDto);
        Token token = tokenService.generateAndSaveTokenFor(user);
//        TODO fixme emailSendingServiceImpl.sendAccountActivationEmail(token);
    }

    public void activateNewAccountBy(String tokenValue) {
        Token token = tokenService.getOneByValue(tokenValue);
        Long userId = token.getUser().getId();
        userService.activateUserById(userId);
    }

    @Transactional
    public void restore(String email) {
        try {
            restoreUserPassword(email);
        } catch (UserNotFoundException hidden) {
            // To hide from a client that user not found
        }
    }

    @Transactional
    public void setNewPassword(String tokenValue, String newPassword) {
        Token token = tokenService.getOneByValue(tokenValue);
        Long userId = token.getUser().getId();
        userService.setNewPasswordForUser(userId, newPassword);
    }

    private void restoreUserPassword(String email) {
        User user = userService.getOne(email);
        if (!user.isEnabled()) {
            return;
        }
        Token token = tokenService.generateAndSaveTokenFor(user);
//        TODO emailSendingServiceImpl.sendPasswordChangingEmail(token);
    }

    private TokenDto loginByCredentials(LoginDto login) {
        User user = userService.getOne(login.username());
        validatePasswordCorrect(login.password(), user.getPassword());
        String token = jwtTokenService.generateJwtToken(user);
        return userMapper.toToken(user, token);
    }

    private void validatePasswordCorrect(String raw, String encoded) {
        if (!passwordEncoder.matches(raw, encoded)) {
            throw new IllegalPasswordException();
        }
    }

    private void validateEmailDoesNotExist(String email) {
        if (userService.isExistsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }
    }
}
