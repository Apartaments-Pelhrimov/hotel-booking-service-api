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

package ua.mibal.booking.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.mibal.booking.application.component.TokenGenerator;
import ua.mibal.booking.application.port.jpa.TokenRepository;
import ua.mibal.booking.config.properties.TokenProps;
import ua.mibal.booking.domain.Token;
import ua.mibal.booking.domain.User;
import ua.mibal.booking.model.exception.entity.TokenNotFoundException;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class TokenService {
    private final TokenRepository tokenRepository;
    private final TokenGenerator tokenGenerator;
    private final TokenProps tokenProps;

    @Transactional
    public Token generateAndSaveTokenFor(User user) {
        Token token = generateTokenFor(user);
        return tokenRepository.save(token);
    }

    @Transactional
    public Token getOneByValue(String tokenValue) {
        return tokenRepository.findNotExpiredByValue(tokenValue)
                .orElseThrow(() -> new TokenNotFoundException(tokenValue));
    }

    public int clearExpiredTokens() {
        return tokenRepository.deleteExpired();
    }

    private Token generateTokenFor(User user) {
        String value = tokenGenerator.generateTokenValue();
        return Token.of(value, user, tokenProps.validForMinutes());
    }
}
