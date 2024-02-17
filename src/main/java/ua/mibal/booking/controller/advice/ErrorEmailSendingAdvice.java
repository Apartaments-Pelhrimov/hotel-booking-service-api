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

package ua.mibal.booking.controller.advice;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ua.mibal.booking.controller.advice.model.ApiError;
import ua.mibal.booking.model.exception.marker.InternalServerException;
import ua.mibal.email.api.EmailSendingService;

import java.util.Locale;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Profile("heroku")
@RestControllerAdvice
public class ErrorEmailSendingAdvice {
    private static final Logger log = LoggerFactory.getLogger(ErrorEmailSendingAdvice.class);
    private final EmailSendingService emailSendingService;
    private final ExceptionHandlerAdvice exceptionHandlerAdvice;

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ApiError> sendErrorEmailToDevelopers(InternalServerException e,
                                                               Locale locale) {
//        TODO FIXME emailSendingService.sendErrorEmailToDevelopers(e);
//        log.info("Email with exception sent to developers: {}", emailProps.developers());
        return exceptionHandlerAdvice.handleInternalServerException(e, locale);
    }

    @PostConstruct
    private void postConstruct() {
        log.info("Initialized {}", getClass().getSimpleName());
//        log.info("Developers: {}", emailProps.developers());
    }
}
