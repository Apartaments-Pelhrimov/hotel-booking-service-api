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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ua.mibal.booking.config.properties.EmailProps;
import ua.mibal.booking.model.entity.Token;
import ua.mibal.booking.model.entity.User;
import ua.mibal.booking.model.exception.marker.InternalServerException;
import ua.mibal.booking.service.email.model.Email;
import ua.mibal.booking.service.email.model.EmailContent;
import ua.mibal.booking.service.email.model.EmailType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class EmailBuilder_UnitTest {

    private EmailBuilder builder;

    @Mock
    private EmailContentProvider contentProvider;
    @Mock
    private Session session;
    @Mock
    private EmailProps props;

    @Mock
    private EmailType type;
    @Mock
    private Token token;
    @Mock
    private User user;
    @Mock
    private EmailContent content;
    @Mock
    private Email email;
    @Mock
    private InternalServerException e;

    private MockedStatic<Email> mockedEmail;

    @BeforeEach
    void setup() {
        mockedEmail = mockStatic(Email.class);
        builder = new EmailBuilder(contentProvider, session, props);
    }

    @AfterEach
    void after() {
        mockedEmail.close();
    }

    @Test
    void buildUserEmail() {
        String recipient = "recipientEmail";
        String sender = "senderEmail";

        when(token.getUser())
                .thenReturn(user);
        when(user.getEmail())
                .thenReturn(recipient);
        when(contentProvider.getEmailContentBy(type, token))
                .thenReturn(content);
        when(props.username())
                .thenReturn(sender);
        when(Email.of(session, sender, recipient, content))
                .thenReturn(email);

        Email actual = builder.buildUserEmail(type, token);

        assertEquals(email, actual);
    }

    @Test
    void buildDeveloperEmail() {
        String recipients = "devEmail1,devEmail2";
        String sender = "senderEmail";

        when(props.developers())
                .thenReturn(recipients);
        when(contentProvider.getEmailContentByException(e))
                .thenReturn(content);
        when(props.username())
                .thenReturn(sender);
        when(Email.of(session, sender, recipients, content))
                .thenReturn(email);

        Email actual = builder.buildDeveloperEmail(e);

        assertEquals(email, actual);
    }
}
