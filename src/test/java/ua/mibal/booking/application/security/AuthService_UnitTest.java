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

package ua.mibal.booking.application.security;

import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.mibal.booking.application.UserService;
import ua.mibal.booking.application.port.email.EmailSendingService;
import ua.mibal.booking.application.port.email.model.Email;
import ua.mibal.booking.application.security.component.TemplateEmailFactory;
import ua.mibal.booking.application.security.jwt.JwtTokenService;
import ua.mibal.booking.domain.Token;
import ua.mibal.booking.domain.User;
import ua.mibal.booking.model.dto.LoginDto;
import ua.mibal.booking.model.dto.auth.RegistrationDto;
import ua.mibal.booking.model.dto.auth.TokenDto;
import ua.mibal.booking.model.exception.EmailAlreadyExistsException;
import ua.mibal.booking.model.exception.entity.UserNotFoundException;
import ua.mibal.booking.model.exception.marker.NotAuthorizedException;
import ua.mibal.booking.model.mapper.UserMapper;
import ua.mibal.test.annotation.UnitTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class AuthService_UnitTest {

    private AuthService service;

    @Mock
    private JwtTokenService jwtTokenService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserService userService;
    @Mock
    private TokenService tokenService;
    @Mock
    private EmailSendingService emailSendingService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TemplateEmailFactory emailFactory;

    @Mock
    private User user;
    @Mock
    private Token token;
    @Mock
    private TokenDto expectedAuthDto;
    @Mock
    private RegistrationDto registrationDto;
    @Mock
    private Email email;

    @BeforeEach
    void setup() {
        service = new AuthService(jwtTokenService, userMapper, userService, tokenService, emailSendingService, passwordEncoder, emailFactory);
    }

    @ParameterizedTest
    @InstancioSource
    void login(LoginDto loginDto, String token, String originalUserPassword) {

        when(userService.getOne(loginDto.username()))
                .thenReturn(user);
        when(user.getPassword())
                .thenReturn(originalUserPassword);
        when(passwordEncoder.matches(loginDto.password(), originalUserPassword))
                .thenReturn(true);
        when(jwtTokenService.generateJwtToken(user))
                .thenReturn(token);
        when(userMapper.toToken(user, token))
                .thenReturn(expectedAuthDto);

        var actual = service.login(loginDto);

        assertEquals(expectedAuthDto, actual);
    }

    @ParameterizedTest
    @CsvSource({
            // Username          Password                   Token      Correct username  Correct password
            "incorrect_username, correct_password,          any_token, correct_username, correct_password",
            "incorrect_username, incorrect_password,        any_token, correct_username, correct_password",
            "correct_username,   incorrect_password,        any_token, correct_username, correct_password",
    })
    void login_should_wrap_UserNotFoundException_and_IllegalPasswordException_into_NotAuthorizedException(
            String username, String password, String token, String correctUsername, String correctPassword
    ) {
        LoginDto loginDto = new LoginDto(username, password);

        if (username.equals(correctUsername)) {
            when(userService.getOne(username))
                    .thenReturn(user);
        } else {
            when(userService.getOne(username))
                    .thenThrow(UserNotFoundException.class);
        }
        when(user.getPassword())
                .thenReturn(correctPassword);
        when(passwordEncoder.matches(password, correctPassword))
                .thenReturn(password.equals(correctPassword));
        when(jwtTokenService.generateJwtToken(user))
                .thenReturn(token);
        when(userMapper.toToken(user, token))
                .thenReturn(expectedAuthDto);

        assertThrows(NotAuthorizedException.class,
                () -> service.login(loginDto));
    }

    @Test
    void register() {
        String notExistingEmail = "not_existing_email";
        String password = "test_pass";
        when(registrationDto.email()).thenReturn(notExistingEmail);
        when(registrationDto.password()).thenReturn(password);

        when(userService.isExistsByEmail(notExistingEmail))
                .thenReturn(false);
        when(userService.save(registrationDto))
                .thenReturn(user);
        when(tokenService.generateAndSaveTokenFor(user))
                .thenReturn(token);
        when(emailFactory.getAccountActivationEmail(token))
                .thenReturn(email);

        service.register(registrationDto);

        verify(emailSendingService, times(1))
                .send(email);
    }

    @Test
    void register_should_throw_EmailAlreadyExistsException() {
        String existingEmail = "existing_email";
        when(registrationDto.email()).thenReturn(existingEmail);

        when(userService.isExistsByEmail(existingEmail))
                .thenReturn(true);

        verifyNoMoreInteractions(userService);

        EmailAlreadyExistsException e = assertThrows(
                EmailAlreadyExistsException.class,
                () -> service.register(registrationDto)
        );
        assertEquals(
                new EmailAlreadyExistsException(existingEmail).getMessage(),
                e.getMessage()
        );
        verifyNoInteractions(tokenService, emailSendingService);
    }

    @Test
    void activateNewAccountBy() {
        String code = "CODE";
        long id = 1L;
        when(tokenService.getOneByValue(code))
                .thenReturn(token);
        when(token.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(id);

        service.activateNewAccountBy(code);

        verify(userService, times(1))
                .activateUserById(id);
    }

    @Test
    void restore() {
        String emailStr = "email";
        when(userService.getOne(emailStr))
                .thenReturn(user);
        when(user.isEnabled())
                .thenReturn(true);
        when(tokenService.generateAndSaveTokenFor(user))
                .thenReturn(token);
        when(emailFactory.getPasswordChangingEmail(token))
                .thenReturn(email);

        assertDoesNotThrow(
                () -> service.restore(emailStr)
        );

        verify(emailSendingService, times(1))
                .send(email);
    }

    @Test
    void restore_should_not_throw_exception_if_user_not_found() {
        String email = "not_existing_email";
        when(userService.getOne(email)).thenThrow(UserNotFoundException.class);

        verifyNoMoreInteractions(userService);

        assertDoesNotThrow(
                () -> service.restore(email)
        );

        verifyNoInteractions(tokenService, emailSendingService);
    }

    @Test
    void setNewPassword() {
        String password = "pass";
        String code = "CODE";
        long id = 1L;
        when(tokenService.getOneByValue(code))
                .thenReturn(token);
        when(token.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(id);

        service.setNewPassword(code, password);

        verify(userService, times(1))
                .setNewPasswordForUser(id, password);
    }
}
