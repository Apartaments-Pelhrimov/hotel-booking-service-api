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

package ua.mibal.booking.application.validation;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ua.mibal.booking.application.model.DateRangeValidRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DateRangeValidator_UnitTest {

    private final DateRangeValidator dateRangeValidator =
            new DateRangeValidator();

    @ParameterizedTest
    @CsvSource(value = {
            "2030-01-01, 2030-01-02, true",
            "2023-01-01, 2023-01-01, false",
            "2023-01-01, 2023-01-02, false",
            "2030-01-02, 2030-01-01, false",
            "null,       2030-01-02, false",
            "2030-01-01, null,       false",
    }, nullValues = "null")
    void isValid_LocalDate(LocalDate from, LocalDate to, boolean expected) {
        DateRangeValidRequest dateRangeValidRequest =
                DateRangeValidRequest.of(from, to);

        boolean actual = dateRangeValidator.isValid(
                dateRangeValidRequest, null);

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "2030-01-01T00:00:00, 2030-01-02T00:00:00, true",
            "2030-01-01T00:00:00, 2030-01-01T00:00:01, true",
            "2030-01-01T00:00:00, 2030-01-01T00:00:00, false",
            "2023-01-01T00:00:00, 2023-01-02T00:00:00, false",
            "2030-01-02T00:00:00, 2030-01-01T00:00:00, false",
            "null,                2030-01-02T00:00:00, false",
            "2030-01-01T00:00:00,          null,       false",
    }, nullValues = "null")
    void isValid_LocalDateTime(LocalDateTime from, LocalDateTime to, boolean expected) {
        DateRangeValidRequest dateRangeValidRequest =
                DateRangeValidRequest.of(from, to);

        boolean actual = dateRangeValidator.isValid(
                dateRangeValidRequest, null);

        assertEquals(expected, actual);
    }
}
