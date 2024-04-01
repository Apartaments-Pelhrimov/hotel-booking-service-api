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

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ua.mibal.booking.adapter.in.web.controller.SecurityControllerTest;
import ua.mibal.booking.adapter.in.web.mapper.UserDtoMapper;
import ua.mibal.booking.application.UserService;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.mibal.booking.adapter.in.web.security.TestSecurityJwtUtils.jwt;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@WebMvcTest(UserController.class)
class UserControllerSecurityTest extends SecurityControllerTest {

    @MockBean
    private UserService userService;
    @MockBean
    private UserDtoMapper userDtoMapper;

    private ResultActions responseResult;

    @Test
    void getOne() throws Exception {
        mvc.perform(get("/api/users/me")
                        .contentType(APPLICATION_JSON)
                        .with(jwt("user@email.com", "USER")))
                .andExpect(status().isOk());
    }

    @Test
    void getOneWithoutNeededAuthorities() throws Exception {
        getOneWith("user@email.com", "NOT_USER");

        thenResponseStatusIs(FORBIDDEN);
    }

    @Test
    void getOneWithoutAuthorization() throws Exception {
        getOneWithout();

        thenResponseStatusIs(FORBIDDEN);
    }

    @Test
    void getAccount() throws Exception {
        getAccountWith("user@email.com", "USER");

        thenResponseStatusIs(OK);
    }

    @Test
    void getAccountWithoutNeededAuthorities() throws Exception {
        getAccountWith("user@email.com", "NOT_USER");

        thenResponseStatusIs(FORBIDDEN);
    }

    @Test
    void getAccountWithoutAuthorization() throws Exception {
        getAccountWithout();

        thenResponseStatusIs(FORBIDDEN);
    }

    @Test
    void deleteAccount() throws Exception {
        deleteAccountWith("user@email.com", "USER");

        thenResponseStatusIs(NO_CONTENT);
    }

    @Test
    void deleteAccountWithoutNeededAuthorities() throws Exception {
        deleteAccountWith("user@email.com", "NOT_USER");

        thenResponseStatusIs(FORBIDDEN);
    }

    @Test
    void deleteAccountWithoutAuthorization() throws Exception {
        deleteAccountWithout();

        thenResponseStatusIs(FORBIDDEN);
    }

    @Test
    void changeAccount() throws Exception {
        changeAccountWith("user@email.com", "USER");

        thenResponseStatusIs(NO_CONTENT);
    }

    @Test
    void changeAccountWithoutNeededAuthorities() throws Exception {
        changeAccountWith("user@email.com", "NOT_USER");

        thenResponseStatusIs(FORBIDDEN);
    }

    @Test
    void changeAccountWithoutAuthorization() throws Exception {
        changeAccountWithout();

        thenResponseStatusIs(FORBIDDEN);
    }

    @Test
    void putPassword() throws Exception {
        putPasswordWith("user@email.com", "USER");

        thenResponseStatusIs(NO_CONTENT);
    }

    @Test
    void putPasswordWithoutNeededAuthorities() throws Exception {
        putPasswordWith("user@email.com", "NOT_USER");

        thenResponseStatusIs(FORBIDDEN);
    }

    @Test
    void putPasswordWithoutAuthorization() throws Exception {
        putPasswordWithout();

        thenResponseStatusIs(FORBIDDEN);
    }

    @Test
    void changeNotificationSettings() throws Exception {
        changeNotificationSettingsWith("user@email.com", "USER");

        thenResponseStatusIs(NO_CONTENT);
    }

    @Test
    void changeNotificationSettingsWithoutNeededAuthorities() throws Exception {
        changeNotificationSettingsWith("user@email.com", "NOT_USER");

        thenResponseStatusIs(FORBIDDEN);
    }

    @Test
    void changeNotificationSettingsWithoutAuthorization() throws Exception {
        changeNotificationSettingsWithout();

        thenResponseStatusIs(FORBIDDEN);
    }

    private void changeNotificationSettingsWithout() throws Exception {
        perform(changeNotificationSettingsRequest());
    }

    private void changeNotificationSettingsWith(String username, String role) throws Exception {
        perform(changeNotificationSettingsRequest().with(jwt(username, role)));
    }

    private void putPasswordWithout() throws Exception {
        perform(putPasswordRequest());
    }

    private void putPasswordWith(String username, String role) throws Exception {
        perform(putPasswordRequest().with(jwt(username, role)));
    }

    private void changeAccountWithout() throws Exception {
        perform(changeAccountRequest());
    }

    private void changeAccountWith(String username, String role) throws Exception {
        perform(changeAccountRequest().with(jwt(username, role)));
    }

    private void deleteAccountWithout() throws Exception {
        perform(deleteAccountRequest());
    }

    private void deleteAccountWith(String username, String role) throws Exception {
        perform(deleteAccountRequest().with(jwt(username, role)));
    }

    private void getAccountWithout() throws Exception {
        perform(getAccountRequest());
    }

    private void getAccountWith(String username, String role) throws Exception {
        perform(getAccountRequest().with(jwt(username, role)));
    }

    private void getOneWithout() throws Exception {
        perform(getOneRequest());
    }

    private void getOneWith(String username, String role) throws Exception {
        perform(getOneRequest().with(jwt(username, role)));
    }

    private MockHttpServletRequestBuilder changeNotificationSettingsRequest() {
        return patch("/api/users/me/account/notifications")
                .contentType(APPLICATION_JSON)
                .content("{}");
    }

    private MockHttpServletRequestBuilder putPasswordRequest() {
        return put("/api/users/me/account/password")
                .contentType(APPLICATION_JSON)
                .content("""
                        {
                            "oldPassword": "password123",
                            "newPassword": "password321"
                        }
                        """);
    }

    private MockHttpServletRequestBuilder changeAccountRequest() {
        return patch("/api/users/me/account")
                .contentType(APPLICATION_JSON)
                .content("{}");
    }

    private MockHttpServletRequestBuilder deleteAccountRequest() {
        return delete("/api/users/me/account")
                .contentType(APPLICATION_JSON)
                .content("""
                        {
                          "password": ""
                        }
                        """);
    }

    private MockHttpServletRequestBuilder getAccountRequest() {
        return get("/api/users/me/account");
    }

    private MockHttpServletRequestBuilder getOneRequest() {
        return get("/api/users/me");
    }

    private void perform(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        responseResult = mvc.perform(requestBuilder);
    }

    private void thenResponseStatusIs(HttpStatus status) throws Exception {
        responseResult.andExpect(status().is(status.value()));
    }
}
