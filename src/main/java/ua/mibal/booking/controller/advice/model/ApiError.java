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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import ua.mibal.booking.model.exception.marker.ApiException;

import java.time.ZonedDateTime;
import java.util.Map;

import static java.util.Collections.emptyMap;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ApiError {
    private final ZonedDateTime timestamp = ZonedDateTime.now();
    private Integer status;
    private String error;
    private String message;
    private Map<String, String> fieldErrors;

    protected ApiError(HttpStatus status, String error, String message, Map<String, String> fieldErrors) {
        this(
                status.value(),
                error,
                message,
                fieldErrors
        );
    }

    private ApiError(HttpStatus status, String error, String message) {
        this(
                status.value(),
                error,
                message,
                emptyMap()
        );
    }

    private ApiError(HttpStatus status, Exception e) {
        this(
                status,
                e.getClass().getSimpleName(),
                e.getLocalizedMessage()
        );
    }

    private ApiError(ApiException e, String message) {
        this(
                e.getHttpStatus(),
                e.getCode(),
                message
        );
    }

    public static ApiError ofException(HttpStatus status, Exception e) {
        return new ApiError(status, e);
    }

    public static ApiError of(ApiException e, String message) {
        return new ApiError(e, message);
    }
}
