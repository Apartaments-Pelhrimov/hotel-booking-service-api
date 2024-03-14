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

package ua.mibal.booking.adapter.out.email;

import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import ua.mibal.booking.adapter.out.email.component.MimeEmailBuilder;
import ua.mibal.booking.adapter.out.email.config.properties.EmailProps;
import ua.mibal.booking.adapter.out.email.model.MimeEmail;
import ua.mibal.booking.application.port.email.EmailSendingException;
import ua.mibal.booking.application.port.email.model.Email;
import ua.mibal.booking.application.port.email.model.impl.DefaultEmail;
import ua.mibal.booking.application.port.email.model.impl.DefaultEmailContent;
import ua.mibal.test.annotation.UnitTest;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class DefaultEmailSendingService_UnitTest {

    private final MimeEmailBuilder mimeEmailBuilder = mock();
    private final EmailProps emailProps = new EmailProps("username", "password");
    private final MockedStatic<Transport> mockedTransport = mockStatic(Transport.class);

    private final DefaultEmailSendingService service = new DefaultEmailSendingService(mimeEmailBuilder, emailProps);

    private Email email;
    private MimeEmail mimeEmail;

    {
        when(mimeEmailBuilder.buildBy(email))
                .thenReturn(mimeEmail);
    }

    @AfterEach
    void close() {
        mockedTransport.close();
    }

    @Test
    void send() {
        givenEmail();

        service.send(email);

        thenEmailShouldBeSent();
    }

    @Test
    void send_should_throw_EmailSentFailedException() {
        givenEmail();

        whenTransportThrowsMessagingException();

        thenServiceShouldThrowEmailSendingException();
    }

    private void givenEmail() {
        email = new DefaultEmail(
                "sender",
                "recipient",
                new DefaultEmailContent("subject", "html"));
    }

    private void whenTransportThrowsMessagingException() {
        mockedTransport.when(
                        () -> Transport.send(mimeEmail, "username", "password"))
                .thenThrow(MessagingException.class);
    }

    private void thenEmailShouldBeSent() {
        mockedTransport.verify(
                () -> Transport.send(mimeEmail, "username", "password"),
                times(1)
        );
    }

    private void thenServiceShouldThrowEmailSendingException() {
        assertThrows(
                EmailSendingException.class,
                () -> service.send(email)
        );
    }
}
