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

package ua.mibal.booking.service.email;

import jakarta.mail.Transport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ua.mibal.booking.config.properties.EmailProps;
import ua.mibal.booking.model.entity.ActivationCode;
import ua.mibal.booking.model.exception.marker.InternalServerException;
import ua.mibal.booking.service.email.model.Email;
import ua.mibal.booking.service.email.model.EmailBuilder;
import ua.mibal.booking.service.email.model.EmailType;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static ua.mibal.booking.service.email.model.EmailType.ACCOUNT_ACTIVATION;
import static ua.mibal.booking.service.email.model.EmailType.PASSWORD_CHANGING;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class EmailSendingService_UnitTest {

    private EmailSendingService service;

    @Mock
    private EmailBuilder emailBuilder;
    @Mock
    private EmailProps emailProps;

    @Mock
    private ActivationCode activationCode;
    @Mock
    private Email email;
    @Mock
    private InternalServerException e;

    @BeforeEach
    void setup() {
        service = new EmailSendingService(emailBuilder, emailProps);
    }

    // TODO fixme static mocks in another Thread

    @ParameterizedTest
    @Disabled
    @EnumSource(EmailType.class)
    void sendAccountActivationEmail_mocked_Transport(EmailType emailType) {
        try (MockedStatic<Transport> mockedTransport = mockStatic(Transport.class)) {
            sendAccountActivationEmail(emailType, mockedTransport);
        }
    }

    void sendAccountActivationEmail(EmailType emailType, MockedStatic<Transport> mockedTransport) {
        String username = "username228";
        String password = "pass";

        when(emailProps.username())
                .thenReturn(username);
        when(emailProps.password())
                .thenReturn(password);
        when(emailBuilder.buildUserEmail(emailType, activationCode))
                .thenReturn(email);

        if (emailType == ACCOUNT_ACTIVATION) {
            service.sendAccountActivationEmail(activationCode);
        } else if (emailType == PASSWORD_CHANGING) {
            service.sendPasswordChangingEmail(activationCode);
        } else {
            throw new UnsupportedOperationException("Unsupported %s=%S".formatted(
                    emailType.getClass().getSimpleName(), emailType
            ));
        }

        mockedTransport.verify(
                () -> Transport.send(email, username, password),
                times(1)
        );
    }

    @Test
    @Disabled
    void sendErrorEmailToDevelopers_mocked_Transport() {
        try (MockedStatic<Transport> mockedTransport = mockStatic(Transport.class)) {
            sendErrorEmailToDevelopers(mockedTransport);
        }
    }

    void sendErrorEmailToDevelopers(MockedStatic<Transport> mockedTransport) {
        String username = "username228";
        String password = "pass";

        when(emailProps.username())
                .thenReturn(username);
        when(emailProps.password())
                .thenReturn(password);
        when(emailBuilder.buildDeveloperEmail(e))
                .thenReturn(email);

        service.sendErrorEmailToDevelopers(e);

        mockedTransport.verify(
                () -> Transport.send(email, username, password),
                times(1)
        );
    }
}
