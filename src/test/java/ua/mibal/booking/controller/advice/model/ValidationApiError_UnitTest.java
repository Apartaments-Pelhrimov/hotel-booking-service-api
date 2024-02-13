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

package ua.mibal.booking.controller.advice.model;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ua.mibal.booking.test.annotations.UnitTest;
import ua.mibal.booking.test.model.TestBindingResult;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class ValidationApiError_UnitTest {

    private static final Map<String, String> fieldErrors = Map.of(
            "field1", "errorField1",
            "field2", "errorField2"
    );

    @Test
    void of_should_map_errors_correctly() {
        HttpStatus httpStatus = HttpStatus.NO_CONTENT;
        MethodArgumentNotValidException e = methodArgumentNotValidExceptionFrom(fieldErrors);

        ValidationApiError actual = ValidationApiError.of(httpStatus, e);
        assertThat(fieldErrors, is(actual.getFieldErrors()));
    }

    private MethodArgumentNotValidException methodArgumentNotValidExceptionFrom(Map<String, String> fieldErrors) {
        return new MethodArgumentNotValidException((MethodParameter) null, new TestBindingResult(fieldErrors));
    }
}
