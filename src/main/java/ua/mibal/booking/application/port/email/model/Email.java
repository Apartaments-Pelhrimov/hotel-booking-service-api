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

package ua.mibal.booking.application.port.email.model;

/**
 * The {@code Email} interface defines the contract for representing an email message.
 * Implementations of this interface encapsulate essential information about an email, including
 * the sender's address, recipient addresses, and the content of the email.
 *
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public interface Email {

    String getSender();

    String getRecipients();

    /**
     * Retrieves the content of this email.
     *
     * @return An {@code EmailContent} object containing the subject and message body of the email.
     */
    EmailContent getContent();
}
