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

package ua.mibal.booking.service.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ua.mibal.booking.model.entity.User;
import ua.mibal.booking.repository.UserRepository;
import ua.mibal.test.annotation.UnitTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class UserDetailsSecurityService_UnitTest {

    private UserDetailsSecurityService service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private User user;

    @BeforeEach
    void setup() {
        service = new UserDetailsSecurityService(userRepository);
    }

    @Test
    void loadUserByUsername() {
        String existingEmail = "email";

        when(userRepository.findByEmail(existingEmail))
                .thenReturn(Optional.of(user));

        UserDetails actual = service.loadUserByUsername(existingEmail);

        assertEquals(user, actual);
    }

    @Test
    void loadUserByUsername_should_throw_UsernameNotFoundException() {
        String notExistingEmail = "example@company.com";

        when(userRepository.findByEmail(notExistingEmail))
                .thenReturn(Optional.empty());

        UsernameNotFoundException e = assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername(notExistingEmail)
        );

        assertTrue(e.getMessage().contains(notExistingEmail));
    }
}
