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

package ua.mibal.booking.service.email.model;

import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import ua.mibal.booking.model.exception.EmailCreationException;
import ua.mibal.booking.service.email.impl.model.Email;
import ua.mibal.booking.service.email.impl.model.EmailContent;
import ua.mibal.booking.test.annotations.UnitTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class Email_UnitTest {

    @Mock
    private Session session;
    @Mock
    private EmailContent content;

    private String sender = "sender@email";
    private String recipients = "recipient1@email,recipient2@email";

    @Mock
    private Properties props;

    @BeforeEach
    void setup() {
        when(session.getProperties())
                .thenReturn(props);
    }

    @Test
    void of() throws MessagingException, IOException {
        String subject = "SUBJECT";
        String body = "CONTENT";

        when(content.subject())
                .thenReturn(subject);
        when(content.body())
                .thenReturn(body);

        Email actual = Email.of(session, sender, recipients, content);

        assertThat(recipientsOf(actual)).containsAll(expectedRecipients());
        assertEquals(subject, actual.getSubject());
        assertEquals(body, actual.getContent());
        assertEquals(sender, addressesToString(actual.getFrom()));
        assertEquals(session, actual.getSession());
    }

    @Test
    void of_should_throw_EmailCreationException() {
        try (MockedStatic<InternetAddress> mockedInternetAddress = mockStatic(InternetAddress.class)) {
            of_should_throw_EmailCreationException_mockedInternetAddress(mockedInternetAddress);
        }
    }

    void of_should_throw_EmailCreationException_mockedInternetAddress(MockedStatic<InternetAddress> mockedInternetAddress) {
        String subject = "SUBJECT";
        String body = "CONTENT";

        when(content.subject())
                .thenReturn(subject);
        when(content.body())
                .thenReturn(body);

        mockedInternetAddress
                .when(() -> InternetAddress.parse(sender))
                .thenThrow(AddressException.class);

        assertThrows(EmailCreationException.class,
                () -> Email.of(session, sender, recipients, content));
    }


    private String addressesToString(Address[] addresses) {
        String[] addressesStrings = Arrays.stream(addresses)
                .map(Address::toString)
                .toArray(String[]::new);
        return String.join(",", addressesStrings);
    }

    private String[] recipientsOf(Email email) throws MessagingException {
        return Arrays.stream(email.getAllRecipients())
                .map(Address::toString)
                .toArray(String[]::new);
    }

    private List<String> expectedRecipients() {
        return asList(recipients.split(","));
    }
}
