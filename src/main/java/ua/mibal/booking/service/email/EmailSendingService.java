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

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import ua.mibal.booking.model.entity.ActivationCode;
import ua.mibal.booking.model.entity.User;

import java.util.Date;
import java.util.Map;
import java.util.Properties;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Service
public class EmailSendingService {
    private final Session session;
    private final ClasspathFileReader fileReader;
    private final TemplateEngine templateEngine;
    private final String username;
    private final String password;

    public EmailSendingService(Environment env, ClasspathFileReader fileReader, TemplateEngine templateEngine) {
        this.fileReader = fileReader;
        this.templateEngine = templateEngine;
        this.session = getSessionByProperties(env);
        this.username = env.getProperty("mail.user");
        this.password = env.getProperty("mail.password");
    }

    private Session getSessionByProperties(Environment env) {
        Properties props = new Properties();
        props.put("mail.smtp.host", env.getProperty("mail.smtp.host"));
        props.put("mail.smtp.port", env.getProperty("mail.smtp.port"));
        props.put("mail.smtp.starttls.enable", env.getProperty("mail.smtp.starttls.enable"));
        props.put("mail.debug", env.getProperty("mail.debug"));
        return Session.getInstance(props, null);
    }

    private void send(String recipient, String subject, Object message) {
        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(username);
            msg.setRecipients(Message.RecipientType.TO, recipient);
            msg.setSubject(subject);
            msg.setSentDate(new Date());
            msg.setContent(message, "text/html");
            Transport.send(msg, username, password);
        } catch (MessagingException e) {
            throw new RuntimeException("Email send failed", e);
        }
    }

    private void sendCode(EmailType type, User user, String code) {
        String sourceHtmlPage = fileReader.read(type.getTemplatePath());
        String filledPage = templateEngine.insert(sourceHtmlPage, Map.of(
                "user", user,
                "link", type.getFrontLink(code)
        ));
        send(user.getEmail(), type.getSubject(), filledPage);
    }

    public void sendActivationCode(User user, ActivationCode activationCode) {
        sendCode(EmailType.ACCOUNT_ACTIVATION, user, activationCode.getCode());
    }

    public void sendPasswordChangingCode(User user, ActivationCode activationCode) {
        sendCode(EmailType.PASSWORD_CHANGING, user, activationCode.getCode());
    }
}
