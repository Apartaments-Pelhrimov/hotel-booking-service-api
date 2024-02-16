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
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import ua.mibal.booking.service.email.impl.exception.EmailCreationException;
import ua.mibal.booking.service.email.impl.model.MimeEmail;
import ua.mibal.booking.test.annotations.UnitTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class MimeEmail_UnitTest {

    @Mock
    private Session session;

    @BeforeEach
    void setup() {
        when(session.getProperties())
                .thenReturn(mock(Properties.class));
    }

    @ParameterizedTest
    @InstancioSource
    void of(String sender, String recipients, String subject, String body)
            throws MessagingException, IOException {

        MimeEmail actual = MimeEmail.of(session, sender, recipients, subject, body);

        assertEquals(recipients, recipientsOf(actual));
        assertEquals(subject, actual.getSubject());
        assertEquals(body, actual.getContent());
        assertEquals(sender, addressesToString(actual.getFrom()));
        assertEquals(session, actual.getSession());
    }

    @ParameterizedTest
    @InstancioSource
    void of_should_throw_EmailCreationException(String sender, String recipients, String subject, String body) {
        try (MockedStatic<InternetAddress> mockedInternetAddress = mockStatic(InternetAddress.class)) {
            of_should_throw_EmailCreationException_mockedInternetAddress(sender, recipients, subject, body, mockedInternetAddress);
        }
    }

    void of_should_throw_EmailCreationException_mockedInternetAddress(String sender, String recipients, String subject, String body,
                                                                      MockedStatic<InternetAddress> mockedInternetAddress) {
        mockedInternetAddress
                .when(() -> InternetAddress.parse(sender))
                .thenThrow(AddressException.class);

        assertThrows(EmailCreationException.class,
                () -> MimeEmail.of(session, sender, recipients, subject, body));
    }


    private String addressesToString(Address[] addresses) {
        String[] addressesStrings = Arrays.stream(addresses)
                .map(Address::toString)
                .toArray(String[]::new);
        return String.join(",", addressesStrings);
    }

    private String recipientsOf(MimeEmail mimeEmail) throws MessagingException {
        return Arrays.stream(mimeEmail.getAllRecipients())
                .map(Address::toString)
                .collect(Collectors.joining(","));
    }
}
