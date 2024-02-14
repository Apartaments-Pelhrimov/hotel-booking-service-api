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

package ua.mibal.booking.service.email.impl.model;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import ua.mibal.booking.model.exception.EmailCreationException;

import static jakarta.mail.Message.RecipientType.TO;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class MimeEmail extends MimeMessage {

    protected MimeEmail(Session session,
                        String sender,
                        String recipients,
                        String subject,
                        String body) throws MessagingException {
        super(session);
        setFrom(sender);
        setRecipients(TO, recipients);
        setSubject(subject, "UTF-8");
        setContent(body, "text/html; charset=UTF-8");
    }

    /**
     * @param sender     comma separated address strings
     * @param recipients comma separated address strings
     */
    public static MimeEmail of(Session session,
                               String sender,
                               String recipients,
                               String subject,
                               String body) {
        try {
            return new MimeEmail(session, sender, recipients, subject, body);
        } catch (MessagingException e) {
            throw new EmailCreationException(e);
        }
    }
}
