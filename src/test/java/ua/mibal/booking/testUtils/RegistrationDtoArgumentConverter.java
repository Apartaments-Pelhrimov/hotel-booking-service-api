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

package ua.mibal.booking.testUtils;

import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import ua.mibal.booking.application.dto.auth.RegistrationDto;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class RegistrationDtoArgumentConverter extends SimpleArgumentConverter {

    @Override
    protected RegistrationDto convert(Object source, Class<?> targetType) throws ArgumentConversionException {
        if (source == null) {
            return null;
        }
        if (source instanceof String s) {
            String[] args = s.split(" ");
            return registrationDtoByArgs(args);
        }
        throw new IllegalArgumentException("Conversion from " + source.getClass() + " to "
                                           + targetType + " not supported.");
    }

    private RegistrationDto registrationDtoByArgs(String[] args) {
        return new RegistrationDto(args[0], args[1], args[2], args[3], args[4]);
    }
}
