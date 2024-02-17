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
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ua.mibal.booking.config.properties.TokenProps;
import ua.mibal.booking.model.entity.Token;
import ua.mibal.booking.model.entity.User;
import ua.mibal.booking.model.exception.entity.TokenNotFoundException;
import ua.mibal.booking.repository.TokenRepository;
import ua.mibal.test.annotation.UnitTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class TokenService_UnitTest {

    private TokenService service;

    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private TokenGenerationService tokenGenerationService;
    @Mock
    private TokenProps tokenProps;

    @Mock
    private User user;
    @Mock
    private Token token;

    @BeforeEach
    void setup() {
        service = new TokenService(tokenRepository, tokenGenerationService, tokenProps);
    }

    @Test
    void generateAndSaveTokenFor_should_generate_and_save() {
        String tokenValue = "code";
        int validForMinutes = 100500;

        when(tokenGenerationService.generateTokenValue())
                .thenReturn(tokenValue);
        when(tokenProps.validForMinutes())
                .thenReturn(validForMinutes);

        service.generateAndSaveTokenFor(user);

        verify(tokenRepository, times(1))
                .save(Token.of(tokenValue, user, validForMinutes));
    }

    @Test
    void getOneByValue() {
        String tokenValue = "code";
        when(tokenRepository.findNotExpiredByValue(tokenValue))
                .thenReturn(Optional.of(token));

        var actual = service.getOneByValue(tokenValue);

        assertEquals(token, actual);
    }

    @Test
    void getOneByValue_should_throw_TokenNotFoundException() {
        String tokenValue = "code";
        when(tokenRepository.findNotExpiredByValue(tokenValue))
                .thenReturn(Optional.empty());

        assertThrows(TokenNotFoundException.class,
                () -> service.getOneByValue(tokenValue));
    }

    @Test
    void clearExpiredTokens() {
        int deletedCount = 123;

        when(tokenRepository.deleteExpired())
                .thenReturn(deletedCount);

        int actual = service.clearExpiredTokens();

        assertEquals(deletedCount, actual);
    }
}
