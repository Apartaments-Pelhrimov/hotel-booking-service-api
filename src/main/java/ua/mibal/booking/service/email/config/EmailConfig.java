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

package ua.mibal.booking.service.email.config;

import jakarta.mail.Session;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import ua.mibal.booking.service.email.config.properties.EmailProps;

import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@EnableConfigurationProperties(EmailProps.class)
@Configuration
public class EmailConfig {

    @Bean
    public Session getSessionByProperties(Environment env) {
        Properties props = new Properties();
        props.put("mail.smtp.host", Objects.requireNonNull(env.getProperty("mail.smtp.host")));
        props.put("mail.smtp.port", Objects.requireNonNull(env.getProperty("mail.smtp.port")));
        props.put("mail.smtp.starttls.enable", Objects.requireNonNull(env.getProperty("mail.smtp.starttls.enable")));
        Optional.ofNullable(env.getProperty("mail.debug"))
                .ifPresent(val -> props.put("mail.debug", val));
        return Session.getInstance(props);
    }
}
