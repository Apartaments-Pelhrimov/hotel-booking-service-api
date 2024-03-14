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

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.mibal.booking.adapter.out.email.model.MimeEmail;
import ua.mibal.booking.application.port.email.EmailSendingException;
import ua.mibal.booking.application.port.email.model.Email;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Component
public class MimeEmailBuilder {
    private final Session session;

    public MimeEmail buildBy(Email email) {
        try {
            return buildMimeEmail(email);
        } catch (MessagingException e) {
            throw new EmailSendingException("Exception while building email", e);
        }
    }

    private MimeEmail buildMimeEmail(Email email) throws MessagingException {
        return new MimeEmail(
                session,
                email.getSender(),
                email.getRecipients(),
                email.getContent().getSubject(),
                email.getContent().getBody()
        );
    }
}
