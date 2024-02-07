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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ua.mibal.booking.model.dto.auth.AuthResponseDto;
import ua.mibal.booking.model.dto.auth.RegistrationDto;
import ua.mibal.booking.model.entity.Token;
import ua.mibal.booking.model.entity.User;
import ua.mibal.booking.model.exception.EmailAlreadyExistsException;
import ua.mibal.booking.model.exception.entity.UserNotFoundException;
import ua.mibal.booking.model.mapper.UserMapper;
import ua.mibal.booking.service.UserService;
import ua.mibal.booking.service.email.EmailSendingService;
import ua.mibal.booking.service.security.jwt.JwtTokenService;

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
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
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
    private User user;
    @Mock
    private Token token;
    @Mock
    private AuthResponseDto expectedAuthDto;
    @Mock
    private RegistrationDto registrationDto;

    @BeforeEach
    void setup() {
        service = new AuthService(jwtTokenService, userMapper, userService, tokenService, emailSendingService);
    }

    @Test
    void login() {
        String token = "test_token";
        when(jwtTokenService.generateJwtToken(user)).thenReturn(token);
        when(userMapper.toAuthResponse(user, token)).thenReturn(expectedAuthDto);

        var actual = service.login(user);

        assertEquals(expectedAuthDto, actual);
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

        service.register(registrationDto);

        verify(emailSendingService, times(1))
                .sendAccountActivationEmail(token);
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
        String email = "email";
        when(userService.getOne(email))
                .thenReturn(user);
        when(user.isEnabled())
                .thenReturn(true);
        when(tokenService.generateAndSaveTokenFor(user))
                .thenReturn(token);

        assertDoesNotThrow(
                () -> service.restore(email)
        );
        verify(emailSendingService, times(1))
                .sendPasswordChangingEmail(token);
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
