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

package ua.mibal.booking.adapter.in.web.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import ua.mibal.booking.adapter.in.web.controller.ControllerTest;
import ua.mibal.booking.application.UserService;
import ua.mibal.booking.application.model.ChangeNotificationSettingsForm;
import ua.mibal.booking.application.model.ChangeUserForm;
import ua.mibal.booking.domain.NotificationSettings;
import ua.mibal.booking.domain.Phone;
import ua.mibal.booking.domain.Role;
import ua.mibal.booking.domain.User;

import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.mibal.booking.adapter.in.web.security.TestSecurityJwtUtils.jwt;
import static ua.mibal.booking.domain.Role.USER;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@WebMvcTest(UserController.class)
class UserControllerTest extends ControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void getOne() throws Exception {
        givenUserServiceWithUser("Mykhailo", "Balakhon", "user@email.com", "photo_key_123", USER, "+380123456789", new NotificationSettings(true, true));

        whenGetOneThenShouldBeResponse(OK, """
                {
                  "firstName": "Mykhailo",
                  "lastName": "Balakhon",
                  "photo": "http://localhost/api/photos/photo_key_123",
                  "role": "USER"
                }
                """);
    }

    @Test
    void getAccount() throws Exception {
        givenUserServiceWithUser("Mykhailo", "Balakhon", "user@email.com", "photo_key_123", USER, "+380123456789", new NotificationSettings(true, true));

        whenGetAccountThenShouldBeResponse(OK, """
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
                """);
    }

    @Test
    void deleteAccountWithInvalidBody() throws Exception {
        whenDeleteAccountThenShouldBeStatus(null, BAD_REQUEST);

        verify(userService, never()).delete(any(), any());
    }

    @ParameterizedTest
    @CsvSource({"password", "''", "123", "'    '"})
    void deleteAccount(String password) throws Exception {
        whenDeleteAccountThenShouldBeStatus(password, NO_CONTENT);

        verify(userService).delete("user@email.com", password);
    }

    @ParameterizedTest
    @CsvSource({
            //                  Invalid firstName
            // firstName            lastName                phone
            "'invalid First Name',  valid,                  +420123456789",
            "in,                    valid,                  +420123456789",
            "'',                    valid,                  +420123456789",
            "'   ',                 valid,                  +420123456789",

            //                  Invalid lastName
            // firstName            lastName                phone
            "valid,                 'invalid First Name',   +420123456789",
            "valid,                 in,                     +420123456789",
            "valid,                 '',                     +420123456789",
            "valid,                 '   ',                  +420123456789",

            //                   Invalid phone
            // firstName            lastName                phone
            "valid,                 valid,                  invalidPhone",
            "valid,                 valid,                  420123456789",
            "valid,                 valid,                  123",
            "valid,                 valid,                  123456789",
            "valid,                 valid,                  '+420 1234 567 89'",
    })
    void changeAccountWithInvalidBody(String firstName, String lastName, String phone) throws Exception {
        whenChangeThenShouldBeStatus(firstName, lastName, phone, BAD_REQUEST);

        verify(userService, never()).change(any(), any());
    }

    @ParameterizedTest
    @CsvSource(value = {
            //              Fields can be null
            // firstName        lastName            phone
            "Valid,             Valid,              +420123456789",
            "valid,             valid,              null",
            "valid,             null,               null",
            "null,              null,               null",
            "null,              null,               +420123456789",
            "null,              valid,              +420123456789",
            "valid,             valid,              +420123456789",
    }, nullValues = "null")
    void changeAccount(String firstName, String lastName, String phone) throws Exception {
        whenChangeThenShouldBeStatus(firstName, lastName, phone, NO_CONTENT);

        verify(userService).change("user@email.com", new ChangeUserForm(
                firstName, lastName, phone
        ));
    }

    @ParameterizedTest
    @CsvSource(value = {
            //              The new password should be longer than 8,
            //      without spaces and contain at least one letter and a number.

            //       Invalid newPassword
            // oldPassword          newPassword
            "valid,                 null",
            "valid,                 invalid",
            "valid,                 inval1",
            "valid,                 'inval id123'",
            "valid,                 123",

            //       Invalid oldPassword
            // oldPassword          newPassword
            "null,                 ValidPassword123",
    }, nullValues = "null")
    void putPasswordWithInvalidBody(String oldPassword, String newPassword) throws Exception {
        whenPutPasswordThenShouldBeStatus(oldPassword, newPassword, BAD_REQUEST);

        verify(userService, never()).putPassword(any(), any(), any());
    }

    @ParameterizedTest
    @CsvSource(value = {
            //              The new password should be longer than 8,
            //      without spaces and contain at least one letter and a number.

            // oldPassword          newPassword
            "valid,                 ValidPassword123",
            "valid,                 12345678p",
            "valid,                 qwerty123123",
    }, nullValues = "null")
    void putPassword(String oldPassword, String newPassword) throws Exception {
        whenPutPasswordThenShouldBeStatus(oldPassword, newPassword, NO_CONTENT);

        verify(userService).putPassword("user@email.com", oldPassword, newPassword);
    }

    @ParameterizedTest
    @CsvSource(value = {
            //            Fields can be any
            // receiveOrderEmails    receiveNewsEmails
            "true,                  true",
            "true,                  false",
            "false,                 true",
            "false,                 false",
            "null,                  null",
    }, nullValues = "null")
    void changeNotificationSettings(Boolean receiveOrderEmails,
                                    Boolean receiveNewsEmails) throws Exception {
        whenChangeNotificationSettingsThenShouldBeStatus(receiveOrderEmails, receiveNewsEmails, NO_CONTENT);

        verify(userService).changeNotificationSettings("user@email.com", new ChangeNotificationSettingsForm(
                receiveOrderEmails, receiveNewsEmails
        ));
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

    private void whenGetOneThenShouldBeResponse(HttpStatus status, String json) throws Exception {
        mvc.perform(get("/api/users/me")
                        .with(jwt("user@email.com")))
                .andExpect(status().is(status.value()))
                .andExpect(content().json(json, true));
    }

    private void whenGetAccountThenShouldBeResponse(HttpStatus status, String json) throws Exception {
        mvc.perform(get("/api/users/me/account")
                        .with(jwt("user@email.com")))
                .andExpect(status().is(status.value()))
                .andExpect(content().json(json, true));
    }

    private void whenDeleteAccountThenShouldBeStatus(String password, HttpStatus status) throws Exception {
        String body = deleteAccountBody(password);

        mvc.perform(delete("/api/users/me/account")
                        .with(jwt("user@email.com"))
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().is(status.value()));
    }

    private void whenChangeThenShouldBeStatus(String firstName, String lastName, String phone, HttpStatus status) throws Exception {
        String body = changeAccountBody(firstName, lastName, phone);

        mvc.perform(patch("/api/users/me/account")
                        .with(jwt("user@email.com"))
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().is(status.value()));
    }

    private void whenPutPasswordThenShouldBeStatus(String oldPassword, String newPassword, HttpStatus status) throws Exception {
        String body = putPasswordBody(oldPassword, newPassword);

        mvc.perform(put("/api/users/me/account/password")
                        .with(jwt("user@email.com"))
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().is(status.value()));
    }

    private void whenChangeNotificationSettingsThenShouldBeStatus(Boolean receiveOrderEmails,
                                                                  Boolean receiveNewsEmails,
                                                                  HttpStatus status) throws Exception {
        String body = changeNotificationSettingsBody(receiveOrderEmails, receiveNewsEmails);

        mvc.perform(patch("/api/users/me/account/notifications")
                        .with(jwt("user@email.com"))
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().is(status.value()));
    }

    private String deleteAccountBody(String password)
            throws JsonProcessingException {
        HashMap<String, Object> json = new HashMap<>();
        json.put("password", password);
        return mapper.writeValueAsString(json);
    }

    private String changeAccountBody(String firstName, String lastName, String phone)
            throws JsonProcessingException {
        HashMap<String, Object> json = new HashMap<>();
        json.put("firstName", firstName);
        json.put("lastName", lastName);
        json.put("phone", phone);
        return mapper.writeValueAsString(json);
    }

    private String putPasswordBody(String oldPassword, String newPassword)
            throws JsonProcessingException {
        HashMap<String, Object> json = new HashMap<>();
        json.put("oldPassword", oldPassword);
        json.put("newPassword", newPassword);
        return mapper.writeValueAsString(json);
    }

    private String changeNotificationSettingsBody(Boolean receiveOrderEmails, Boolean receiveNewsEmails)
            throws JsonProcessingException {
        HashMap<String, Object> json = new HashMap<>();
        json.put("receiveOrderEmails", receiveOrderEmails);
        json.put("receiveNewsEmails", receiveNewsEmails);
        return mapper.writeValueAsString(json);
    }
}
