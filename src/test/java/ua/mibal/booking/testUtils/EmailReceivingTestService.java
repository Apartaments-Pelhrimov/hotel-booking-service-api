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

package ua.mibal.booking.testUtils;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Component
public class EmailReceivingTestService {

    private final Session session;
    private final String host;
    private final String username;
    private final String password;

    public EmailReceivingTestService(Environment env) {
        this.session = getSessionByProperties(env);
        this.host = env.getProperty("mail.imap.host");
        this.username = env.getProperty("mail.user");
        this.password = env.getProperty("mail.password");
    }

    public Message[] get(MailFolder mailFolder) {
        try (Store store = session.getStore()) {
            store.connect(host, username, password);
            try (Folder folder = store.getFolder(mailFolder.getName())) {
                folder.open(Folder.READ_ONLY);
                Message[] lastTenMessages = folder.getMessages(
                        folder.getMessageCount() - 5, folder.getMessageCount());
                for (Message message : lastTenMessages) {
                    System.out.println(message.getSubject());
                }
                return lastTenMessages;
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private Session getSessionByProperties(Environment env) {
        Properties props = new Properties();
        props.put("mail.imap.host", env.getProperty("mail.imap.host"));
        props.put("mail.imap.port", env.getProperty("mail.imap.port"));
        props.put("mail.imap.starttls.enable", env.getProperty("mail.imap.starttls.enable"));
        props.put("mail.debug", env.getProperty("mail.debug"));
        props.put("mail.store.protocol", env.getProperty("mail.store.protocol"));
        props.put("mail.imap.ssl.trust", env.getProperty("mail.imap.ssl.trust"));
        return Session.getInstance(props, null);
    }

    @RequiredArgsConstructor
    @Getter
    public enum MailFolder {
        INBOX("Inbox");
        private final String name;
    }
}
