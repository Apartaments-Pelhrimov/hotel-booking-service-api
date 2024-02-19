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

package ua.mibal.booking.adapter.in.web.advice.model;

import org.instancio.junit.InstancioSource;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.http.HttpStatus;
import ua.mibal.booking.model.exception.marker.ApiException;
import ua.mibal.test.annotation.UnitTest;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class ApiError_UnitTest {

    @ParameterizedTest
    @InstancioSource
    void ofException(HttpStatus status, Exception e) {
        ApiError actual = ApiError.ofException(status, e);

        assertThat(actual.getStatus(), is(status.value()));
        assertThat(actual.getMessage(), is(e.getLocalizedMessage()));
    }

    @ParameterizedTest
    @InstancioSource
    void of(HttpStatus status, String message) {
        ApiException e = mock(ApiException.class);
        when(e.getHttpStatus())
                .thenReturn(status);

        ApiError actual = ApiError.of(e, message);

        assertThat(actual.getStatus(), is(status.value()));
        assertThat(actual.getMessage(), is(message));
    }

    @ParameterizedTest
    @InstancioSource
    void ApiError_with_fieldErrors(HttpStatus status,
                                   String error,
                                   String message,
                                   String field, String fieldError) {
        ApiError actual = new ApiError(status, error, message, Map.of(field, fieldError));

        assertThat(actual.getStatus(), is(status.value()));
        assertThat(actual.getMessage(), is(message));
        assertThat(actual.getFieldErrors(), is(Map.of(field, fieldError)));
    }
}
