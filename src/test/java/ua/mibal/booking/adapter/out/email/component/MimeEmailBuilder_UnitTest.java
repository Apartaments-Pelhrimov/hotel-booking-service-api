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

package ua.mibal.booking.adapter.out.email.component;

import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ua.mibal.booking.adapter.out.email.model.MimeEmail;
import ua.mibal.booking.application.port.email.EmailSendingException;
import ua.mibal.booking.application.port.email.model.Email;
import ua.mibal.booking.application.port.email.model.impl.DefaultEmail;
import ua.mibal.booking.application.port.email.model.impl.DefaultEmailContent;
import ua.mibal.test.annotation.UnitTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class MimeEmailBuilder_UnitTest {

    private final Session session = mock();

    private final MimeEmailBuilder builder = new MimeEmailBuilder(session);

    private Email email;
    private MimeEmail mimeEmail;

    {
        when(session.getProperties())
                .thenReturn(mock(Properties.class));
    }

    @Test
    void build() {
        givenEmail("sender", "recipient1,recipient2", "subject", "body");

        whenMimeEmailBuilt();

        thenMimeEmailShouldContain("sender", List.of("recipient1", "recipient2"), "subject", "body");
    }

    @ParameterizedTest
    @CsvSource({
            // illegal email addresses
            "email@@a.com,  recipient1,     subject,    body",
            "sender,        email@@a.com,   subject,    body",
    })
    void build_should_throw_if_passed_Email_is_incorrect(String sender,
                                                         String recipients,
                                                         String subject,
                                                         String body) {
        givenEmail(sender, recipients, subject, body);

        assertThrows(EmailSendingException.class,
                () -> whenMimeEmailBuilt());
    }

    private void givenEmail(String sender, String recipients, String subject, String body) {
        email = new DefaultEmail(
                sender, recipients, new DefaultEmailContent(subject, body)
        );
    }

    private void whenMimeEmailBuilt() {
        mimeEmail = builder.buildBy(email);
    }

    private void thenMimeEmailShouldContain(String sender,
                                            List<String> recipients,
                                            String subject,
                                            String body) {
        try {
            thenMimeEmailShouldContainExactly(sender, recipients, subject, body);
        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void thenMimeEmailShouldContainExactly(String sender,
                                                   List<String> recipients,
                                                   String subject,
                                                   String body) throws MessagingException, IOException {
        assertThat(getSenderAddress(mimeEmail)).isEqualTo(sender);
        assertThat(getRecipientAddresses(mimeEmail)).containsAll(recipients);

        assertThat(mimeEmail.getSubject()).isEqualTo(subject);
        assertThat(mimeEmail.getContent()).isEqualTo(body);
        assertThat(mimeEmail.getSession()).isEqualTo(session);
    }

    private String getSenderAddress(MimeEmail mimeEmail) throws MessagingException {
        return mimeEmail.getFrom()[0].toString();
    }

    private List<String> getRecipientAddresses(MimeEmail mimeEmail) throws MessagingException {
        return Arrays.stream(mimeEmail.getAllRecipients())
                .map(Address::toString)
                .toList();
    }
}
