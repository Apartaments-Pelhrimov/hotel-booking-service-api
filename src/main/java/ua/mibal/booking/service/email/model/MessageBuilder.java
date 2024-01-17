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

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

import java.util.Date;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class MessageBuilder {

    private String senderEmail;
    private String recipientEmail;
    private String subject;
    private String content;
    private Session session;

    public MimeMessage buildMimeMessage() throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(senderEmail);
        message.setRecipients(Message.RecipientType.TO, recipientEmail);
        message.setSubject(subject);
        message.setSentDate(new Date());
        message.setContent(content, "text/html");
        return message;
    }

    public MessageBuilder sender(String senderEmail) {
        this.senderEmail = senderEmail;
        return this;
    }

    public MessageBuilder recipient(String recipientEmail) {
        this.recipientEmail = recipientEmail;
        return this;
    }

    public MessageBuilder subject(String subject) {
        this.subject = subject;
        return this;
    }

    public MessageBuilder content(String content) {
        this.content = content;
        return this;
    }

    public MessageBuilder session(Session session) {
        this.session = session;
        return this;
    }
}
