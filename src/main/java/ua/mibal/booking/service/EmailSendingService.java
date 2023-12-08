/*
 * Copyright (c) 2023. Mykhailo Balakhon mailto:9mohapx9@gmail.com
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

package ua.mibal.booking.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.mibal.booking.model.entity.ActivationCode;
import ua.mibal.booking.model.entity.User;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Service
public class EmailSendingService {
    private final static Logger log = LoggerFactory.getLogger(EmailSendingService.class);

    public void sendActivationCode(User user, ActivationCode activationCode) {
        // TODO
        log.info("Sent ActivationCode to email={} code={}", user.getEmail(), activationCode.getCode());
    }

    public void sendPasswordChangingCode(User user, ActivationCode activationCode) {
        log.info("Sent PasswordChangingCode to email={} code={}", user.getEmail(), activationCode.getCode());
    }
}
