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

package ua.mibal.booking.service.email.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
public enum EmailType {

    ACCOUNT_ACTIVATION(
            "mail.account-activation.subject",
            "https://apartmany-pe.cz/activate"
    ),
    PASSWORD_CHANGING(
            "mail.password-changing.subject",
            "https://apartmany-pe.cz/changePass"
    ),
    EXCEPTION_REPORT(
            "mail.exception-report.subject",
            null
    );

    private final String subjectCode;
    @Getter
    private final String frontLink;

    public String getSubject(MessageSource messageSource) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(subjectCode, new Object[0], locale);
    }

    public String getTemplateName() {
        return name().toLowerCase();
    }
}
