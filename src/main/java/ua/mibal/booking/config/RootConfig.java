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

package ua.mibal.booking.config;

import jakarta.mail.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import ua.mibal.booking.config.properties.ActivationCodeProps;
import ua.mibal.booking.config.properties.AwsProps;
import ua.mibal.booking.config.properties.CalendarProps;
import ua.mibal.booking.config.properties.EmailProps;
import ua.mibal.booking.model.entity.User;
import ua.mibal.booking.model.entity.embeddable.Phone;
import ua.mibal.booking.model.entity.embeddable.Role;
import ua.mibal.booking.repository.UserRepository;

import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@EnableConfigurationProperties({
        AwsProps.class,
        AwsProps.AwsBucketProps.class,
        CalendarProps.class,
        CalendarProps.ReservationDateTimeProps.class,
        CalendarProps.ICalProps.class,
        ActivationCodeProps.class,
        EmailProps.class,
})
@Configuration
public class RootConfig {
    private final AwsProps awsProps;

    @Bean
    public S3Client create() {
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                awsProps.accessKeyId(),
                                awsProps.secretAccessKey()
                        )))
                .region(awsProps.region())
                .build();
    }

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

    @Bean
    public CommandLineRunner setup(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            User testUser = new User();
            testUser.setFirstName("Test");
            testUser.setLastName("User");
            testUser.setEmail("user@company.com");
            testUser.setPhone(new Phone("+380951234567"));
            testUser.setPassword(passwordEncoder.encode("password1"));
            testUser.setRole(Role.ROLE_USER);
            testUser.setEnabled(true);

            User testAdmin = new User();
            testAdmin.setFirstName("Test");
            testAdmin.setLastName("Admin");
            testAdmin.setEmail("admin@company.com");
            testAdmin.setPhone(new Phone("+380951234567"));
            testAdmin.setPassword(passwordEncoder.encode("password1"));
            testAdmin.setRole(Role.ROLE_MANAGER);
            testAdmin.setEnabled(true);

            if (!userRepository.existsByEmail(testUser.getEmail())) {
                userRepository.save(testUser);
            }
            if (!userRepository.existsByEmail(testAdmin.getEmail())) {
                userRepository.save(testAdmin);
            }
        };
    }
}
