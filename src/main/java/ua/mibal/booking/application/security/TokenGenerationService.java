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

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.mibal.booking.config.properties.TokenProps;

import java.util.Random;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class TokenGenerationService {
    private static final String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                                                "abcdefghijklmnopqrstuvwxyz" +
                                                "0123456789" + "0123456789" +
                                                "0123456789" + "0123456789";
    private static final Random RANDOM = new Random();

    private final TokenProps tokenProps;

    public String generateTokenValue() {
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < tokenProps.length(); i++) {
            char randomChar = getRandomChar();
            token.append(randomChar);
        }
        return token.toString();
    }

    private char getRandomChar() {
        int maxIndex = ALLOWED_CHARS.length();
        int randomIndex = RANDOM.nextInt(maxIndex);
        return ALLOWED_CHARS.charAt(randomIndex);
    }
}
