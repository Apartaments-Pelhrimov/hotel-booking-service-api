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

package ua.mibal.booking.adapter.out.email;

import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.mibal.booking.adapter.out.email.component.MimeEmailBuilder;
import ua.mibal.booking.adapter.out.email.config.properties.EmailProps;
import ua.mibal.booking.adapter.out.email.exception.EmailSentFailedException;
import ua.mibal.booking.adapter.out.email.model.MimeEmail;
import ua.mibal.booking.application.port.email.EmailSendingService;
import ua.mibal.booking.application.port.email.model.Email;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class DefaultEmailSendingService implements EmailSendingService {
    private final MimeEmailBuilder mimeEmailBuilder;
    private final EmailProps emailProps;

    public void send(Email email) {
        MimeEmail mimeEmail = mimeEmailBuilder.buildBy(email);
        sendAsync(mimeEmail);
    }

    // TODO use ExecutorService
    private void sendAsync(MimeEmail mimeEmail) {
        new Thread(() -> {
            try {
                send(mimeEmail);
            } catch (MessagingException e) {
                throw new EmailSentFailedException(e);
            }
        }, "Email-sending-Thread").start();
    }

    private synchronized void send(MimeEmail mimeEmail) throws MessagingException {
        Transport.send(mimeEmail, emailProps.username(), emailProps.password());
    }
}
