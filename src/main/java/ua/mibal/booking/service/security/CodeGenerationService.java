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

package ua.mibal.booking.service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.mibal.booking.config.properties.ActivationCodeProps;

import java.util.Random;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class CodeGenerationService {
    private final static String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                                                "abcdefghijklmnopqrstuvwxyz" +
                                                "0123456789" + "0123456789" +
                                                "0123456789" + "0123456789";

    private final ActivationCodeProps activationCodeProps;

    public String generateCode() {
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        int length = ALLOWED_CHARS.length();
        int count = 0;
        while (count++ != activationCodeProps.length()) {
            int index = random.nextInt(length);
            char val = ALLOWED_CHARS.charAt(index);
            stringBuilder.append(val);
        }
        return stringBuilder.toString();
    }
}
