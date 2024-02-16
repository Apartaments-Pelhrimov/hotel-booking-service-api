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

package ua.mibal.booking.service.email.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import ua.mibal.booking.service.email.api.Email;
import ua.mibal.booking.service.email.impl.component.MimeEmailBuilder;
import ua.mibal.booking.service.email.impl.config.properties.EmailProps;
import ua.mibal.booking.service.email.impl.exception.EmailSentFailedException;
import ua.mibal.booking.service.email.impl.model.MimeEmail;
import ua.mibal.booking.test.annotations.UnitTest;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Disabled("Static Mocks are not allowed in MultiThreading")
@UnitTest
class DefaultEmailSendingService_UnitTest {

    private DefaultEmailSendingService service;

    @Mock
    private MimeEmailBuilder mimeEmailBuilder;
    @Mock
    private EmailProps emailProps;

    @Mock
    private Email email;
    @Mock
    private MimeEmail mimeEmail;

    private MockedStatic<Transport> mockedTransport;

    @BeforeEach
    void setup() {
        service = new DefaultEmailSendingService(mimeEmailBuilder, emailProps);
        mockedTransport = mockStatic(Transport.class);
    }

    @AfterEach
    void close() {
        mockedTransport.close();
    }

    @ParameterizedTest
    @InstancioSource
    void send(String username, String password) {
        when(emailProps.username())
                .thenReturn(username);
        when(emailProps.password())
                .thenReturn(password);
        when(mimeEmailBuilder.buildBy(email))
                .thenReturn(mimeEmail);

        service.send(email);

        mockedTransport.verify(
                () -> Transport.send(mimeEmail, username, password),
                times(1)
        );
    }

    @ParameterizedTest
    @InstancioSource
    void send_should_throw_EmailSentFailedException(String username, String password) {
        when(emailProps.username())
                .thenReturn(username);
        when(emailProps.password())
                .thenReturn(password);
        when(mimeEmailBuilder.buildBy(email))
                .thenReturn(mimeEmail);

        mockedTransport.when(
                        () -> Transport.send(mimeEmail, username, password))
                .thenThrow(MessagingException.class);

        assertThrows(
                EmailSentFailedException.class,
                () -> service.send(email)
        );
    }
}
