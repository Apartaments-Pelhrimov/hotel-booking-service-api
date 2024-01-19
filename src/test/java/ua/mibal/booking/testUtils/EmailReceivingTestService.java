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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.mibal.booking.config.properties.EmailProps;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Component
public class EmailReceivingTestService {

    private final Session session;

    private final EmailProps emailProps;
    @Value("${mail.imap.host}")
    private String host;

    public Message[] get(MailFolder mailFolder) {
        try (Store store = session.getStore()) {
            store.connect(host, emailProps.username(), emailProps.password());
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

    @RequiredArgsConstructor
    @Getter
    public enum MailFolder {
        INBOX("Inbox");
        private final String name;
    }
}
