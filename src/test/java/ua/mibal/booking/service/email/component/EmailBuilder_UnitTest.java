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

import jakarta.mail.Session;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import ua.mibal.booking.service.email.EmailConfiguration;
import ua.mibal.booking.service.email.EmailConfiguration.EmailContent;
import ua.mibal.booking.service.email.impl.component.EmailBuilder;
import ua.mibal.booking.service.email.impl.model.Email;
import ua.mibal.booking.test.annotations.UnitTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class EmailBuilder_UnitTest {

    private EmailBuilder builder;

    @Mock
    private Session session;

    @Mock
    private EmailConfiguration configuration;
    @Mock
    private EmailContent content;
    @Mock
    private Email email;

    private MockedStatic<Email> mockedEmail;

    @BeforeEach
    void setup() {
        mockedEmail = mockStatic(Email.class);
        builder = new EmailBuilder(session);
    }

    @AfterEach
    void after() {
        mockedEmail.close();
    }

    @ParameterizedTest
    @InstancioSource
    void build(String sender, String recipients, String subject, String body) {
        when(configuration.getSender()).thenReturn(sender);
        when(configuration.getRecipients()).thenReturn(recipients);
        when(configuration.getContent()).thenReturn(content);
        when(content.getSubject()).thenReturn(subject);
        when(content.getBody()).thenReturn(body);

        mockedEmail.when(() -> Email.of(session, sender, recipients, subject, body))
                .thenReturn(email);

        Email actual = builder.build(configuration);

        assertEquals(email, actual);
    }
}
