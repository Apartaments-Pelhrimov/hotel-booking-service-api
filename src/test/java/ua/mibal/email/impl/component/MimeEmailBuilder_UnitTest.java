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

package ua.mibal.email.impl.component;

import jakarta.mail.Session;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import ua.mibal.test.annotation.UnitTest;
import ua.mibal.email.api.model.Email;
import ua.mibal.email.api.model.EmailContent;
import ua.mibal.email.impl.model.MimeEmail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class MimeEmailBuilder_UnitTest {

    private MimeEmailBuilder builder;

    @Mock
    private Session session;

    @Mock
    private Email email;
    @Mock
    private EmailContent content;
    @Mock
    private MimeEmail mimeEmail;

    private MockedStatic<MimeEmail> mockedEmail;

    @BeforeEach
    void setup() {
        mockedEmail = mockStatic(MimeEmail.class);
        builder = new MimeEmailBuilder(session);
    }

    @AfterEach
    void after() {
        mockedEmail.close();
    }

    @ParameterizedTest
    @InstancioSource
    void build(String sender, String recipients, String subject, String body) {
        when(email.getSender()).thenReturn(sender);
        when(email.getRecipients()).thenReturn(recipients);
        when(email.getContent()).thenReturn(content);
        when(content.getSubject()).thenReturn(subject);
        when(content.getBody()).thenReturn(body);

        mockedEmail.when(() -> MimeEmail.of(session, sender, recipients, subject, body))
                .thenReturn(mimeEmail);

        MimeEmail actual = builder.buildBy(email);

        assertEquals(mimeEmail, actual);
    }
}
