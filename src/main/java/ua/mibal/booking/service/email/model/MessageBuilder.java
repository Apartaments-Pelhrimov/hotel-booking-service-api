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
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Date;
import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class MessageBuilder {

    private String[] recipientEmails;
    private String senderEmail;
    private String subject;
    private String content;
    private Session session;

    public MimeMessage buildMimeMessage() throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(senderEmail);
        Address[] addresses = emailsToAddresses(recipientEmails);
        message.setRecipients(Message.RecipientType.TO, addresses);
        message.setSubject(subject, "UTF-8");
        message.setSentDate(new Date());
        message.setContent(content, "text/html; charset=UTF-8");
        return message;
    }

    public MessageBuilder sender(String senderEmail) {
        this.senderEmail = senderEmail;
        return this;
    }

    public MessageBuilder recipients(List<String> recipientEmails) {
        this.recipientEmails = recipientEmails.toArray(String[]::new);
        return this;
    }

    public MessageBuilder recipient(String recipientEmail) {
        this.recipientEmails = new String[]{recipientEmail};
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

    private Address[] emailsToAddresses(String[] emails) throws AddressException {
        Address[] addresses = new Address[emails.length];
        for (int i = 0; i < emails.length; i++) {
            String email = emails[i];
            Address emailAddress = new InternetAddress(email);
            addresses[i] = emailAddress;
        }
        return addresses;
    }
}
