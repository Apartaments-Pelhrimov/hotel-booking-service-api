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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.MessageSource;
import ua.mibal.booking.model.entity.Token;
import ua.mibal.booking.model.entity.User;
import ua.mibal.booking.model.exception.marker.InternalServerException;
import ua.mibal.booking.service.email.model.EmailContent;
import ua.mibal.booking.service.email.model.EmailType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class EmailContentProvider_UnitTest {

    private EmailContentProvider emailContentProvider;

    @Mock
    private ClasspathFileReader classpathFileReader;
    @Mock
    private TemplateEngine templateEngine;
    @Mock
    private MessageSource messageSource;

    @Mock
    private EmailType emailType;
    @Mock
    private Token token;
    @Mock
    private InternalServerException e;

    @BeforeEach
    void setup() {
        emailContentProvider = new EmailContentProvider(classpathFileReader, templateEngine, messageSource);
    }

    @Test
    void getEmailContentBy() {
        String subject = "test subject";
        String templatePath = "test template path";
        String templateContent = "raw template";
        String code = "code";
        String frontLink = "frontLink";
        String insertedTemplate = "inserted template";

        when(emailType.getSubject(messageSource))
                .thenReturn(subject);
        when(emailType.getTemplatePath(messageSource))
                .thenReturn(templatePath);

        when(classpathFileReader.read(templatePath))
                .thenReturn(templateContent);
        when(token.getUser())
                .thenReturn(new User());
        when(token.getValue())
                .thenReturn(code);
        when(emailType.getFrontLinkFor(code))
                .thenReturn(frontLink);
        when(templateEngine.insertIntoTemplate(eq(templateContent), anyMap()))
                .thenReturn(insertedTemplate);

        EmailContent actual =
                emailContentProvider.getEmailContentBy(emailType, token);

        assertEquals(subject, actual.subject());
        assertEquals(insertedTemplate, actual.body());
    }

    @Test
    void getEmailContentByException() {
        String stackTrace = "test stack trace";

        when(e.getStackTraceMessage())
                .thenReturn(stackTrace);

        EmailContent actual =
                emailContentProvider.getEmailContentByException(e);

        assertTrue(actual.subject().contains(e.getClass().getName()));
        assertTrue(actual.body().contains(stackTrace));
    }
}
