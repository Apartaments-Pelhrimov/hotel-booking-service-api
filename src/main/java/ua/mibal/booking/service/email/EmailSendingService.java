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

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.mibal.booking.config.properties.EmailProps;
import ua.mibal.booking.model.entity.ActivationCode;
import ua.mibal.booking.model.exception.marker.InternalServerException;
import ua.mibal.booking.model.exception.service.EmailSentFailedException;
import ua.mibal.booking.service.email.model.EmailType;
import ua.mibal.booking.service.email.model.MessageBuilder;

import java.util.Map;

import static ua.mibal.booking.service.email.model.EmailType.ACCOUNT_ACTIVATION;
import static ua.mibal.booking.service.email.model.EmailType.PASSWORD_CHANGING;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class EmailSendingService {
    private final Session session;
    private final ClasspathFileReader fileReader;
    private final TemplateEngine templateEngine;
    private final EmailProps emailProps;

    public void sendActivationCode(ActivationCode activationCode) {
        sendCodeFor(ACCOUNT_ACTIVATION, activationCode);
    }

    public void sendPasswordChangingCode(ActivationCode activationCode) {
        sendCodeFor(PASSWORD_CHANGING, activationCode);
    }

    public void sendErrorEmailToDevelopers(InternalServerException e) {
        MessageBuilder messageBuilder = getMessageBuilderForDevelopers(e);
        sendAsyncEmail(messageBuilder);
    }

    private void sendCodeFor(EmailType type, ActivationCode code) {
        MessageBuilder messageBuilder = getMessageBuilderBy(type, code);
        sendAsyncEmail(messageBuilder);
    }

    private void sendAsyncEmail(MessageBuilder messageBuilder) {
        new Thread(() -> {
            try {
                sendEmail(messageBuilder);
            } catch (MessagingException e) {
                throw new EmailSentFailedException(e);
            }
        }, "Email-sending-Thread").start();
    }

    private synchronized void sendEmail(MessageBuilder messageBuilder)
            throws MessagingException {
        MimeMessage message = messageBuilder.buildMimeMessage();
        Transport.send(message, emailProps.username(), emailProps.password());
    }

    private MessageBuilder getMessageBuilderForDevelopers(InternalServerException e) {
        return new MessageBuilder()
                .session(session)
                .recipients(emailProps.developers())
                .subject("Internal server error " + e.getClass())
                .content(e.getFormattedStackTrace());
    }

    private MessageBuilder getMessageBuilderBy(EmailType type,
                                               ActivationCode code) {
        String emailContent = getInsertedTemplateBy(type, code);
        return new MessageBuilder()
                .recipient(code.getUser().getEmail())
                .sender(emailProps.username())
                .session(session)
                .subject(type.getSubject())
                .content(emailContent);
    }

    private String getInsertedTemplateBy(EmailType type, ActivationCode code) {
        String sourceHtmlTemplate = fileReader.read(type.getTemplatePath());
        return templateEngine.insertIntoTemplate(sourceHtmlTemplate, Map.of(
                "user", code.getUser(),
                "link", type.getFrontLink(code.getCode())
        ));
    }
}
