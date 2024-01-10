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

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Component
public class EmailUtils {
    private final EmailReceivingTestService emailReceivingTestService;
    private final Date initDate = new Date();

    public boolean messageReceived(String messageSubject) {
        Message[] messages = emailReceivingTestService.get(EmailReceivingTestService.MailFolder.INBOX);
        return Arrays.stream(messages).anyMatch(mess -> {
            try {
                return mess.getSentDate().after(initDate) &&
                       mess.getSubject().equals(messageSubject);
            } catch (MessagingException e) {
                e.printStackTrace();
                return false;
            }
        });
    }
}
