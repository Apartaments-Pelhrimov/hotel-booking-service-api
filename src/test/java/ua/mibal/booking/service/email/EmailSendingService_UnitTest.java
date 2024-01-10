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

package ua.mibal.booking.service.email;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ua.mibal.booking.config.Config;
import ua.mibal.booking.model.entity.ActivationCode;
import ua.mibal.booking.model.entity.User;
import ua.mibal.booking.testUtils.DataGenerator;
import ua.mibal.booking.testUtils.EmailUtils;

import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static ua.mibal.booking.service.email.EmailType.ACCOUNT_ACTIVATION;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EmailSendingService.class)
@TestPropertySource(locations = "classpath:application.yaml")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Import(Config.class)
class EmailSendingService_UnitTest {
    private final User user = spy(DataGenerator.testUser());
    @Autowired
    private EmailSendingService service;
    @Autowired
    private EmailUtils emailUtils;
    @Autowired
    private Environment env;

    @MockBean
    private ClasspathFileReader classpathFileReader;
    @MockBean
    private TemplateEngine templateEngine;
    @Mock
    private ActivationCode activationCode;

    @Test
    void sendActivationCode() throws InterruptedException {
        when(user.getEmail()).thenReturn(env.getProperty("mail.user"));
        when(activationCode.getCode()).thenReturn("CODE");
        when(classpathFileReader.read(ACCOUNT_ACTIVATION.getTemplatePath())).thenReturn("TEMPLATE");
        when(templateEngine.insert("TEMPLATE", Map.of(
                "user", user,
                "link", ACCOUNT_ACTIVATION.getFrontLink("CODE")
        ))).thenReturn("INSERTED_TEMPLATE");

        assertDoesNotThrow(() -> service.sendActivationCode(user, activationCode));

//        Thread.sleep(5 * 1000); // to be confident - the email was sent
        assertTrue(emailUtils.messageReceived(ACCOUNT_ACTIVATION.getSubject()));
    }

    @Test
    void sendPasswordChangingCode() {
    }
}
