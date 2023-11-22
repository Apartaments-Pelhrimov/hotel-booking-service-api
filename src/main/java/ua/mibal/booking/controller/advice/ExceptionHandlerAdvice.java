/*
 * Copyright (c) 2023. Mykhailo Balakhon, mailto:9mohapx9@gmail.com
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

package ua.mibal.booking.controller.advice;

import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import software.amazon.awssdk.core.exception.SdkException;
import ua.mibal.booking.model.exception.EmailAlreadyExistsException;
import ua.mibal.booking.model.exception.IllegalPasswordException;

import java.time.ZonedDateTime;
import java.util.stream.Collectors;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String objErrors = e.getGlobalErrors().stream()
                .map(err -> err.getDefaultMessage() + ".")
                .collect(Collectors.joining(" "));
        String template = "Field %s=%s %s.";
        String fieldErrors = e.getBindingResult()
                .getFieldErrors().stream()
                .map(er -> String.format(template, er.getField(), er.getRejectedValue(), er.getDefaultMessage()))
                .collect(Collectors.joining(" "));
        return ResponseEntity.status(status)
                .body(new ApiError(status, e, (objErrors + " " + fieldErrors).trim()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleValidationException(EntityNotFoundException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status)
                .body(new ApiError(status, e));
    }

    @ExceptionHandler({EmailAlreadyExistsException.class, IllegalPasswordException.class, SdkException.class})
    public ResponseEntity<ApiError> handleEmailAlreadyExistsException(RuntimeException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status)
                .body(new ApiError(status, e));
    }

    @Data
    public static class ApiError {
        private ZonedDateTime timestamp;
        private Integer status;
        private String error;
        private String message;

        public ApiError(Integer status, String error, String message) {
            this.timestamp = ZonedDateTime.now();
            this.status = status;
            this.error = error;
            this.message = message;
        }

        public ApiError(HttpStatus status, Exception e) {
            this(status, e, e.getMessage());
        }

        public ApiError(HttpStatus status, Exception e, String message) {
            this(
                    status.value(),
                    e.getClass().getSimpleName(),
                    message
            );
        }
    }
}
