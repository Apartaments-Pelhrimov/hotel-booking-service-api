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

package ua.mibal.booking.service.email.component;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import ua.mibal.booking.model.entity.Token;
import ua.mibal.booking.model.exception.marker.InternalServerException;
import ua.mibal.booking.service.email.model.EmailContent;
import ua.mibal.booking.service.email.model.EmailType;

import java.util.Map;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Component
public class EmailContentProvider {
    private final ClasspathFileReader fileReader;
    private final TemplateEngine templateEngine;
    private final MessageSource messageSource;

    public EmailContent getEmailContentBy(EmailType type, Token token) {
        String subject = type.getSubject(messageSource);
        String body = generateBody(type, token);
        return new EmailContent(subject, body);
    }

    public EmailContent getEmailContentByException(InternalServerException e) {
        String subject = "Internal server Exception " + e.getClass().getName();
        String body = generateBodyByException(e);
        return new EmailContent(subject, body);
    }

    private String generateBody(EmailType type, Token token) {
        String templatePath = type.getTemplatePath(messageSource);
        String sourceHtmlTemplate = fileReader.read(templatePath);
        return templateEngine.insertIntoTemplate(sourceHtmlTemplate, Map.of(
                "user", token.getUser(),
                "link", type.getFrontLinkTemplate(token.getValue())
        ));
    }

    private String generateBodyByException(InternalServerException e) {
        String stackTrace = e.getStackTraceMessage();
        return "<pre>\n" + stackTrace + "</pre>";
    }
}
