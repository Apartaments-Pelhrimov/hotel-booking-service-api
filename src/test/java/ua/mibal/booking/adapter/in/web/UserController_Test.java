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

package ua.mibal.booking.adapter.in.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ua.mibal.booking.application.UserService;
import ua.mibal.booking.domain.Role;
import ua.mibal.booking.domain.User;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.mibal.booking.domain.Role.USER;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@WebMvcTest(UserController.class)
@ComponentScan(value = "ua.mibal.booking.adapter.in.web.mapper")
class UserController_Test {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    private User user;

    @Test
    @WithMockUser(username = "user@email.com", authorities = "USER")
    void getOne() throws Exception {
        givenUser("Mykhailo", "Balakhon", "user@email.com", "photo_key_123", USER);
        givenUserServiceWithUser();

        mvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer token")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "firstName": "Mykhailo",
                          "lastName": "Balakhon",
                          "photo": "http://localhost/api/photos/photo_key_123",
                          "role": "USER"
                        }
                        """));
    }

    @Test
    void getOneWithoutAuthorizationHeader() throws Exception {
        mvc.perform(get("/api/users/me")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    private void givenUser(String firstName, String lastName,
                           String email, String photoKey, Role role) {
        user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhoto(photoKey);
        user.setRole(role);
    }

    private void givenUserServiceWithUser() {
        when(userService.getOne(user.getEmail()))
                .thenReturn(user);
    }
}
