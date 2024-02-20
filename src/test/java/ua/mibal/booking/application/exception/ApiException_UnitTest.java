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

package ua.mibal.booking.application.exception;

import org.instancio.junit.InstancioSource;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import ua.mibal.test.annotation.UnitTest;

import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class ApiException_UnitTest {

    @Mock
    private MessageSource messageSource;

    @ParameterizedTest
    @InstancioSource
    void getLocalizedMessage(String code, HttpStatus status, Object[] args, Locale locale, String message) {
        when(messageSource.getMessage(code, args, locale))
                .thenReturn(message);

        ApiException actual = new TestApiException(code, status, args);

        String actualMessage = actual.getLocalizedMessage(messageSource, locale);
        assertThat(actualMessage, is(message));
        assertThat(status, is(actual.getHttpStatus()));
    }

    /**
     * @author Mykhailo Balakhon
     * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
     */
    private final static class TestApiException extends ApiException {

        private final Object[] args;

        public TestApiException(String code, HttpStatus status, Object[] args) {
            super(code, status);
            this.args = args;
        }

        @Override
        public Object[] provideArgs() {
            return args;
        }
    }
}
