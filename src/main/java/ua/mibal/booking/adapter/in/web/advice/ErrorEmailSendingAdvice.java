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

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ua.mibal.booking.adapter.in.web.advice.model.ApiError;
import ua.mibal.booking.model.exception.marker.InternalServerException;
import ua.mibal.booking.application.security.component.TemplateEmailFactory;
import ua.mibal.email.api.EmailSendingService;
import ua.mibal.email.api.model.Email;

import java.util.Locale;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Slf4j
@RequiredArgsConstructor
@Profile("heroku")
@RestControllerAdvice
public class ErrorEmailSendingAdvice {
    private final EmailSendingService emailSendingService;
    private final TemplateEmailFactory emailFactory;
    private final ExceptionHandlerAdvice exceptionHandlerAdvice;

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ApiError> sendErrorEmailToDevelopers(InternalServerException e,
                                                               Locale locale) {
        Email email = emailFactory.getExceptionReportEmail(e);
        emailSendingService.send(email);
        log.info("Email with exception sent to developers: {}", email.getRecipients());
        return exceptionHandlerAdvice.handleInternalServerException(e, locale);
    }

    @PostConstruct
    private void logCreated() {
        log.info("Initialized {}", getClass().getSimpleName());
    }
}
