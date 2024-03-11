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

package ua.mibal.booking.application.port.email;

import ua.mibal.booking.application.port.email.model.Email;

/**
 * The {@code EmailSendingService} interface defines the contract for a service responsible for
 * sending emails.
 * Implementations of this interface are expected to provide functionality for sending an
 * {@code Email} object.
 *
 * <p>The {@code send} method is the primary method that should be implemented to initiate the
 * process of sending an email.
 * The {@code Email} parameter encapsulates all necessary information for composing and delivering
 * the email, including sender addresses, recipient addresses, subject, content.
 *
 * <p>Implementing classes should handle the underlying mechanisms of connecting to email servers
 * and ensuring the successful delivery of the message.
 *
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public interface EmailSendingService {

    /**
     * Sends the specified email.
     *
     * @param email The {@code Email} object containing information about the email to be sent,
     *              including sender addresses, recipient addresses, subject, content.
     * @throws EmailSendingException on any error
     */
    void send(Email email);
}
