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

package ua.mibal.booking.application.component;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;
import org.thymeleaf.ITemplateEngine;
import ua.mibal.booking.application.port.email.model.Email;
import ua.mibal.booking.config.properties.ApplicationProps;
import ua.mibal.booking.domain.Token;
import ua.mibal.booking.model.exception.marker.ApiException;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Component
public class TemplateEmailFactory {
    private final ITemplateEngine templateEngine;
    private final ApplicationProps applicationProps;

    // TODO
    public Email getAccountActivationEmail(Token token) {
        throw new NotImplementedException();
    }

    public Email getPasswordChangingEmail(Token token) {
        throw new NotImplementedException();
    }

    public Email getExceptionReportEmail(ApiException e) {
        throw new NotImplementedException();
    }
}
