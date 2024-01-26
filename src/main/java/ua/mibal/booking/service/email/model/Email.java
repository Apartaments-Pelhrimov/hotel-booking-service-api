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

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

import static jakarta.mail.Message.RecipientType.TO;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class Email extends MimeMessage {

    protected Email(Session session,
                    String sender,
                    String recipients,
                    EmailContent emailContent) throws MessagingException {
        super(session);
        setFrom(sender);
        setRecipients(TO, recipients);
        setSubject(emailContent.subject(), "UTF-8");
        setContent(emailContent.body(), "text/html; charset=UTF-8");
    }

    /**
     * @param recipients comma separated address strings
     */
    public static Email of(Session session,
                           String senderEmail,
                           String recipients,
                           EmailContent emailContent) {
        try {
            return new Email(session, senderEmail, recipients, emailContent);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
