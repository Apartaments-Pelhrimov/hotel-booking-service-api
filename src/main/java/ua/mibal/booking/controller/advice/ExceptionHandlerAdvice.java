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

package ua.mibal.booking.controller.advice;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.multipart.MultipartException;
import ua.mibal.booking.model.exception.marker.BadRequestException;
import ua.mibal.booking.model.exception.marker.InternalServerException;
import ua.mibal.booking.model.exception.marker.NotFoundException;

import java.util.stream.Collectors;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RestControllerAdvice
public class ExceptionHandlerAdvice {
    private final static Logger log = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String objErrors = e.getGlobalErrors().stream()
                .map(err -> err.getDefaultMessage() + ".")
                .collect(Collectors.joining(" "));
        String template = "Field %s='%s' %s.";
        String fieldErrors = e.getBindingResult()
                .getFieldErrors().stream()
                .map(er -> String.format(template, er.getField(), er.getRejectedValue(), er.getDefaultMessage()))
                .collect(Collectors.joining(" "));
        return ResponseEntity.status(status)
                .body(ApiError.ofExceptionAndMessage(status, e, (objErrors + " " + fieldErrors).trim()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(Exception e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status)
                .body(ApiError.ofException(status, e));
    }

    @ExceptionHandler({
            BadRequestException.class,
            MultipartException.class
    })
    public ResponseEntity<ApiError> handleEmailAlreadyExistsException(Exception e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status)
                .body(ApiError.ofException(status, e));
    }

    @ExceptionHandler({
            RestClientResponseException.class
    })
    public ResponseEntity<ApiError> handleRestClientResponseException(RestClientResponseException e) {
        HttpStatus status = HttpStatus.valueOf(e.getStatusCode().value());
        return ResponseEntity.status(status)
                .body(ApiError.ofException(status, e));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleOtherException(AccessDeniedException e) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        return ResponseEntity.status(status)
                .body(ApiError.ofException(status, e));
    }

    @ExceptionHandler({
            Exception.class,
            InternalServerException.class
    })
    public ResponseEntity<ApiError> handleOtherException(Exception e) {
        log.error("An Internal error occurred", e);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status)
                .body(ApiError.ofException(status, e));
    }
}
