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

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.stream.Collectors;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class MethodValidationApiError extends ApiError {

    protected final static String
            FIELD_ERROR_MESSAGE_TEMPLATE = "Field %s='%s' %s.";

    protected MethodValidationApiError(HttpStatus status,
                                       Exception exception,
                                       String message) {
        super(status, exception, message);
    }

    public static MethodValidationApiError of(HttpStatus status,
                                              MethodArgumentNotValidException e) {
        String objectErrors = getObjectErrorsMessage(e);
        String fieldErrors = getFieldErrorsMessage(e);
        String message = String.join(" ", objectErrors, fieldErrors);
        return new MethodValidationApiError(status, e, message);
    }

    private static String getFieldErrorsMessage(MethodArgumentNotValidException e) {
        return e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(MethodValidationApiError::reformatFieldErrorMessage)
                .collect(Collectors.joining(" "));
    }

    private static String getObjectErrorsMessage(MethodArgumentNotValidException e) {
        return e.getGlobalErrors()
                .stream()
                .map(err -> err.getDefaultMessage() + ".")
                .collect(Collectors.joining(" "));
    }

    protected static String reformatFieldErrorMessage(FieldError error) {
        String fieldName = error.getField();
        Object fieldValue = error.getRejectedValue();
        String errorMessage = error.getDefaultMessage();
        return FIELD_ERROR_MESSAGE_TEMPLATE.formatted(
                fieldName, fieldValue, errorMessage
        );
    }
}
