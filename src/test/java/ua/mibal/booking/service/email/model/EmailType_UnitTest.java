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

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import ua.mibal.booking.test.annotations.UnitTest;

import java.io.IOException;
import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.mockito.Mockito.when;
import static ua.mibal.booking.service.email.model.EmailType.ACCOUNT_ACTIVATION;
import static ua.mibal.booking.service.email.model.EmailType.PASSWORD_CHANGING;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class EmailType_UnitTest {

    private final Locale locale = Locale.ENGLISH;

    private final String accountActivationSubject = "ACCOUNT_ACTIVATION_SUBJECT";
    private final String passwordChangingSubject = "PASSWORD_CHANGING_SUBJECT";

    private final String accountActivationTemplatePath = "ACCOUNT_ACTIVATION_TEMPLATE//PATH";
    private final String passwordChangingTemplatePath = "PASSWORD_CHANGING_TEMPLATE//PATH";

    @Mock
    private MessageSource messageSource;

    @BeforeEach
    void setUp() {
        LocaleContextHolder.setLocale(locale);

        // subject
        when(messageSource.getMessage("mail.account-activation.subject", new Object[0], locale))
                .thenReturn(accountActivationSubject);
        when(messageSource.getMessage("mail.password-changing.subject", new Object[0], locale))
                .thenReturn(passwordChangingSubject);

        // template path
        when(messageSource.getMessage("mail.account-activation.template-path", new Object[0], locale))
                .thenReturn(accountActivationTemplatePath);
        when(messageSource.getMessage("mail.password-changing.template-path", new Object[0], locale))
                .thenReturn(passwordChangingTemplatePath);
    }

    @Test
    void getSubject() {
        assertThat(ACCOUNT_ACTIVATION.getSubject(messageSource), is(accountActivationSubject));
        assertThat(PASSWORD_CHANGING.getSubject(messageSource), is(passwordChangingSubject));
    }

    @Test
    void getTemplatePath() {
        assertThat(ACCOUNT_ACTIVATION.getTemplatePath(messageSource), is(accountActivationTemplatePath));
        assertThat(PASSWORD_CHANGING.getTemplatePath(messageSource), is(passwordChangingTemplatePath));
    }

    @Ignore("Run test after front server link setup")
    @ParameterizedTest
    @EnumSource(EmailType.class)
    void getFrontLinkFor_PASSWORD_CHANGING(EmailType emailType) throws IOException {
        String token = "activation_token_value";

        String actualLink = emailType.getFrontLinkFor(token);
        HttpUriRequest request = new HttpGet(actualLink);
        HttpResponse httpResponse = HttpClientBuilder.create().build()
                .execute(request);

        assertThat(actualLink, stringContainsInOrder(token));
        assertThat(httpResponse.getStatusLine().getStatusCode(), is(200));
    }
}
