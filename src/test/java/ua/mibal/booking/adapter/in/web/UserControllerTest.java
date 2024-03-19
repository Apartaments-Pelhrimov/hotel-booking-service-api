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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ua.mibal.booking.application.UserService;
import ua.mibal.booking.application.exception.UserNotFoundException;
import ua.mibal.booking.application.model.ChangeNotificationSettingsForm;
import ua.mibal.booking.application.model.ChangeUserForm;
import ua.mibal.booking.domain.NotificationSettings;
import ua.mibal.booking.domain.Phone;
import ua.mibal.booking.domain.Role;
import ua.mibal.booking.domain.User;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.mibal.booking.domain.Role.USER;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@WebMvcTest(UserController.class)
class UserControllerTest extends ControllerTest {

    @MockBean
    private UserService userService;

    @Test
    void getOne() throws Exception {
        givenUserServiceWithUser("Mykhailo", "Balakhon", "user@email.com", "photo_key_123", USER, "+380123456789", new NotificationSettings(true, true));

        mvc.perform(get("/api/users/me")
                        .with(jwt("user@email.com", "USER")))
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
    void getOneNotFound() throws Exception {
        givenUserServiceWithoutUsers();

        mvc.perform(get("/api/users/me")
                        .with(jwt("user@email.com", "USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    void getOneWithoutNeededAuthorities() throws Exception {
        mvc.perform(get("/api/users/me")
                        .with(jwt("user@email.com", "NOT_USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getOneWithoutAuthorization() throws Exception {
        mvc.perform(get("/api/users/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAccount() throws Exception {
        givenUserServiceWithUser("Mykhailo", "Balakhon", "user@email.com", "photo_key_123", USER, "+380123456789", new NotificationSettings(true, true));

        mvc.perform(get("/api/users/me/account")
                        .with(jwt("user@email.com", "USER")))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "firstName": "Mykhailo",
                          "lastName": "Balakhon",
                          "photo": "http://localhost/api/photos/photo_key_123",
                          "role": "USER",
                          "phone": "+380123456789",
                          "email": "user@email.com",
                          "notificationSettings": {
                            "receiveOrderEmails": true,
                            "receiveNewsEmails": true
                          }
                        }
                        """));
    }

    @Test
    void getAccountNotFound() throws Exception {
        givenUserServiceWithoutUsers();

        mvc.perform(get("/api/users/me/account")
                        .with(jwt("user@email.com", "USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAccountWithoutNeededAuthorities() throws Exception {
        mvc.perform(get("/api/users/me/account")
                        .with(jwt("user@email.com", "NOT_USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAccountWithoutAuthorization() throws Exception {
        mvc.perform(get("/api/users/me/account"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteAccount() throws Exception {
        mvc.perform(delete("/api/users/me/account")
                        .with(jwt("user@email.com", "USER"))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isNoContent());

        verify(userService).delete("user@email.com", "password123");
    }

    @Test
    void deleteAccountNotFound() throws Exception {
        givenUserServiceWithoutUsers();

        mvc.perform(delete("/api/users/me/account")
                        .with(jwt("user@email.com", "USER"))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAccountWithoutNeededAuthorities() throws Exception {
        mvc.perform(delete("/api/users/me/account")
                        .with(jwt("user@email.com", "NOT_USER"))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteAccountWithoutAuthorization() throws Exception {
        mvc.perform(delete("/api/users/me/account")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void changeAccount() throws Exception {
        mvc.perform(patch("/api/users/me/account")
                        .with(jwt("user@email.com", "USER"))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Olha",
                                  "lastName": "Tkachenko",
                                  "phone": "+380987654321"
                                }
                                """))
                .andExpect(status().isNoContent());

        verify(userService).change("user@email.com", new ChangeUserForm(
                "Olha", "Tkachenko", "+380987654321"
        ));
    }

    @Test
    void changeAccountNotFound() throws Exception {
        givenUserServiceWithoutUsers();

        mvc.perform(patch("/api/users/me/account")
                        .with(jwt("user@email.com", "USER"))
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound());
    }


    @Test
    void changeAccountWithoutNeededAuthorities() throws Exception {
        mvc.perform(patch("/api/users/me/account")
                        .with(jwt("user@email.com", "NOT_USER"))
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void changeAccountWithoutAuthorization() throws Exception {
        mvc.perform(patch("/api/users/me/account")
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void putPassword() throws Exception {
        mvc.perform(put("/api/users/me/account/password")
                        .with(jwt("user@email.com", "USER"))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "oldPassword": "password123",
                                    "newPassword": "password321"
                                }
                                """))
                .andExpect(status().isNoContent());

        verify(userService).putPassword("user@email.com", "password123", "password321");
    }

    @Test
    void putPasswordNotFound() throws Exception {
        givenUserServiceWithoutUsers();

        mvc.perform(put("/api/users/me/account/password")
                        .with(jwt("user@email.com", "USER"))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "oldPassword": "password123",
                                    "newPassword": "password321"
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void putPasswordWithoutNeededAuthorities() throws Exception {
        mvc.perform(put("/api/users/me/account/password")
                        .with(jwt("user@email.com", "NOT_USER"))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "oldPassword": "password123",
                                    "newPassword": "password321"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void putPasswordWithoutAuthorization() throws Exception {
        mvc.perform(put("/api/users/me/account/password")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "oldPassword": "password123",
                                    "newPassword": "password321"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void changeNotificationSettings() throws Exception {
        mvc.perform(patch("/api/users/me/account/notifications")
                        .with(jwt("user@email.com", "USER"))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "receiveOrderEmails": false,
                                  "receiveNewsEmails": false
                                }
                                """))
                .andExpect(status().isNoContent());

        verify(userService).changeNotificationSettings("user@email.com", new ChangeNotificationSettingsForm(
                false, false
        ));
    }

    @Test
    void changeNotificationSettingsNotFound() throws Exception {
        givenUserServiceWithoutUsers();

        mvc.perform(patch("/api/users/me/account/notifications")
                        .with(jwt("user@email.com", "USER"))
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void changeNotificationSettingsWithoutNeededAuthorities() throws Exception {
        mvc.perform(patch("/api/users/me/account/notifications")
                        .with(jwt("user@email.com", "NOT_USER"))
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void changeNotificationSettingsWithoutAuthorization() throws Exception {
        mvc.perform(patch("/api/users/me/account/notifications")
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    private void givenUserServiceWithUser(String firstName,
                                          String lastName,
                                          String email,
                                          String photoKey,
                                          Role role,
                                          String phone,
                                          NotificationSettings notificationSettings) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhoto(photoKey);
        user.setRole(role);
        user.setPhone(new Phone(phone));
        user.setNotificationSettings(notificationSettings);

        when(userService.getOne(user.getEmail()))
                .thenReturn(user);
    }

    private void givenUserServiceWithoutUsers() {
        Answer<InvocationOnMock> throwNotFoundException = invocation -> {
            String username = invocation.getArgument(0, String.class);
            throw new UserNotFoundException(username);
        };
        when(userService.getOne(any()))
                .then(throwNotFoundException);
        doAnswer(throwNotFoundException)
                .when(userService).delete(any(), any());
        doAnswer(throwNotFoundException)
                .when(userService).putPassword(any(), any(), any());
        doAnswer(throwNotFoundException)
                .when(userService).change(any(), any());
        doAnswer(throwNotFoundException)
                .when(userService).changeNotificationSettings(any(), any());
    }
}
