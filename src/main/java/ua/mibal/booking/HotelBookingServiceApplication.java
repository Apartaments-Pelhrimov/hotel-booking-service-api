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

package ua.mibal.booking;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.mibal.booking.model.entity.User;
import ua.mibal.booking.model.entity.embeddable.Phone;
import ua.mibal.booking.model.entity.embeddable.Role;
import ua.mibal.booking.repository.UserRepository;

import java.util.List;

@SpringBootApplication
public class HotelBookingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelBookingServiceApplication.class, args);
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

            userRepository.saveAll(List.of(testUser, testAdmin));
        };
    }
}
