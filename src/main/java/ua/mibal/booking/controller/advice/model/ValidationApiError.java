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

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.stream.Collectors;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class ValidationApiError extends ApiError {

    protected ValidationApiError(HttpStatus status,
                                 String message) {
        super(status, "bad-request-error.validation", message);
    }

    public static ValidationApiError of(HttpStatus status,
                                        MethodArgumentNotValidException e) {
        String objectErrors = getObjectErrorsMessage(e);
        String fieldErrors = getFieldErrorsMessage(e);
        String message = (objectErrors + " " + fieldErrors).trim();
        return new ValidationApiError(status, message);
    }

    private static String getFieldErrorsMessage(MethodArgumentNotValidException e) {
        return e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(" "));
    }

    private static String getObjectErrorsMessage(MethodArgumentNotValidException e) {
        return e.getGlobalErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(" "));
    }
}
