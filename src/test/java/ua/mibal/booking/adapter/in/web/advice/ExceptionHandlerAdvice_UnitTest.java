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

package ua.mibal.booking.adapter.in.web.advice;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import ua.mibal.booking.adapter.in.web.advice.model.ApiError;
import ua.mibal.booking.adapter.in.web.advice.model.ValidationApiError;
import ua.mibal.booking.application.exception.ApiException;
import ua.mibal.test.annotation.UnitTest;

import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class ExceptionHandlerAdvice_UnitTest {

    private ExceptionHandlerAdvice exceptionHandler;

    @Mock
    private MessageSource errorMessageSource;
    @Mock
    private Locale locale;

    private MockedStatic<ApiError> mockedApiError;
    private MockedStatic<ValidationApiError> mockedValidationApiError;
    @Mock
    private ApiError apiError;
    @Mock
    private ValidationApiError validationApiError;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;
    @Mock
    private MaxUploadSizeExceededException maxUploadSizeExceededException;
    @Mock
    private AccessDeniedException accessDeniedException;
    @Mock
    private ApiException apiException;


    @BeforeEach
    public void setup() {
        exceptionHandler = new ExceptionHandlerAdvice(errorMessageSource);

        mockedApiError = mockStatic(ApiError.class);
        mockedValidationApiError = mockStatic(ValidationApiError.class);
    }

    @AfterEach
    public void shutdown() {
        mockedApiError.close();
        mockedValidationApiError.close();
    }

    @Test
    void handleValidationException() {
        HttpStatus expectedStatus = BAD_REQUEST;

        mockedValidationApiError
                .when(() -> ValidationApiError.of(expectedStatus, methodArgumentNotValidException))
                .thenReturn(validationApiError);

        ResponseEntity<ApiError> actualResponse =
                exceptionHandler.handleValidationException(methodArgumentNotValidException);

        assertThat(actualResponse.getStatusCode(), is(expectedStatus));
        assertThat(actualResponse.getBody(), is(validationApiError));
    }

    @Test
    void handleMaxUploadSizeExceededException() {
        HttpStatus expectedStatus = BAD_REQUEST;

        mockedApiError
                .when(() -> ApiError.ofException(expectedStatus, maxUploadSizeExceededException))
                .thenReturn(apiError);

        ResponseEntity<ApiError> actualResponse =
                exceptionHandler.handleMaxUploadSizeExceededException(maxUploadSizeExceededException);

        assertThat(actualResponse.getStatusCode(), is(expectedStatus));
        assertThat(actualResponse.getBody(), is(apiError));
    }

    @Test
    void handleAccessDeniedException() {
        HttpStatus expectedStatus = FORBIDDEN;

        mockedApiError
                .when(() -> ApiError.ofException(expectedStatus, accessDeniedException))
                .thenReturn(apiError);

        ResponseEntity<ApiError> actualResponse =
                exceptionHandler.handleAccessDeniedException(accessDeniedException);

        assertThat(actualResponse.getStatusCode(), is(expectedStatus));
        assertThat(actualResponse.getBody(), is(apiError));
    }

    @ParameterizedTest
    @EnumSource(HttpStatus.class)
    void handleApiException(HttpStatus expectedStatus) {
        String message = "LOCALIZED_MESSAGE";

        when(apiException.getLocalizedMessage(errorMessageSource, locale))
                .thenReturn(message);
        when(apiException.getHttpStatus())
                .thenReturn(expectedStatus);

        mockedApiError
                .when(() -> ApiError.of(apiException, message))
                .thenReturn(apiError);

        ResponseEntity<ApiError> actualResponse =
                exceptionHandler.handleApiException(apiException, locale);

        assertThat(actualResponse.getStatusCode(), is(expectedStatus));
        assertThat(actualResponse.getBody(), is(apiError));
    }

    @ParameterizedTest
    @EnumSource(HttpStatus.class)
    void handleInternalServerException(HttpStatus expectedStatus) {
        handleApiException(expectedStatus);
    }
}

