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
import org.springframework.stereotype.Component;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import ua.mibal.booking.application.exception.ApiException;
import ua.mibal.booking.application.model.EmailType;
import ua.mibal.booking.application.port.email.model.Email;
import ua.mibal.booking.application.port.email.model.impl.DefaultEmail;
import ua.mibal.booking.application.port.email.model.impl.DefaultEmailContent;
import ua.mibal.booking.config.properties.ApplicationProps;
import ua.mibal.booking.config.properties.TokenProps;
import ua.mibal.booking.domain.Token;

import java.util.Map;

import static ua.mibal.booking.application.model.EmailType.ACCOUNT_ACTIVATION;
import static ua.mibal.booking.application.model.EmailType.PASSWORD_CHANGING;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Component
public class TemplateEmailFactory {
    private final ITemplateEngine templateEngine;
    private final ApplicationProps applicationProps;
    private final TokenProps tokenProps;

    public Email getAccountActivationEmail(Token token) {
        String recipients = token.getUser().getEmail();
        return assembleEmailFor(ACCOUNT_ACTIVATION, recipients, Map.of(
                "token", token,
                "tokenProps", tokenProps
        ));
    }

    public Email getPasswordChangingEmail(Token token) {
        String recipients = token.getUser().getEmail();
        return assembleEmailFor(PASSWORD_CHANGING, recipients, Map.of(
                "token", token,
                "tokenProps", tokenProps
        ));
    }

    public Email getExceptionReportEmail(ApiException e) {
        String recipients = applicationProps.developerEmails();
        return assembleEmailFor(PASSWORD_CHANGING, recipients, Map.of(
                "e", e
        ));
    }

    private Email assembleEmailFor(EmailType type, String recipients, Map<String, Object> vars) {
        String sender = applicationProps.email();
        String subject = type.subject();
        String body = getInsertedTemplate(type, vars);
        return new DefaultEmail(
                sender,
                recipients,
                new DefaultEmailContent(subject, body)
        );
    }

    private String getInsertedTemplate(EmailType type, Map<String, Object> vars) {
        IContext context = new Context(applicationProps.locale(), vars);
        return templateEngine.process(type.templateName(), context);
    }
}
