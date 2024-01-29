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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.mibal.booking.config.properties.EmailProps;
import ua.mibal.booking.model.entity.ActivationCode;
import ua.mibal.booking.model.exception.marker.InternalServerException;
import ua.mibal.booking.service.email.model.Email;
import ua.mibal.booking.service.email.model.EmailContent;
import ua.mibal.booking.service.email.model.EmailType;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Component
public class EmailBuilder {
    private final EmailContentProvider emailContentProvider;
    private final Session session;
    private final EmailProps emailProps;

    public Email buildUserEmail(EmailType type,
                                ActivationCode code) {
        String recipient = code.getUser().getEmail();
        EmailContent emailContent = emailContentProvider.getEmailContentBy(type, code);
        return buildEmailMessageOf(recipient, emailContent);
    }

    public Email buildDeveloperEmail(InternalServerException e) {
        String recipients = emailProps.developers();
        EmailContent emailContent = emailContentProvider.getEmailContentByException(e);
        return buildEmailMessageOf(recipients, emailContent);
    }

    private Email buildEmailMessageOf(String recipients, EmailContent emailContent) {
        String sender = emailProps.username();
        return Email.of(session, sender, recipients, emailContent);
    }
}
