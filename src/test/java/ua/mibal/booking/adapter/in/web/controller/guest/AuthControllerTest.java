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

package ua.mibal.booking.adapter.in.web.controller.guest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import ua.mibal.booking.adapter.in.web.controller.ControllerTest;
import ua.mibal.booking.adapter.in.web.mapper.AuthDtoMapper;
import ua.mibal.booking.adapter.in.web.model.TokenDto;
import ua.mibal.booking.application.AuthService;
import ua.mibal.booking.application.model.RegistrationForm;
import ua.mibal.booking.application.model.RestorePasswordForm;
import ua.mibal.booking.application.model.SetPasswordForm;
import ua.mibal.booking.application.model.TokenForm;

import java.util.HashMap;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@WebMvcTest(AuthController.class)
class AuthControllerTest extends ControllerTest {

    @MockBean
    private AuthService authService;
    @MockBean
    private AuthDtoMapper authDtoMapper;

    @Autowired
    private ObjectMapper mapper;

    private ResultActions responseResult;

    @Test
    void login() throws Exception {
        givenTokenForUser("token_value_123321", "user@email.com", "password");

        whenLogin("user@email.com", "password");

        thenShouldBeStatus(OK);
        thenShouldBeJsonResponse("""
                {
                  "token": "token_value_123321"
                }
                """);
    }

    @ParameterizedTest
    @CsvSource(nullValues = "null", value = {
            "null,    null",
            "null,    correct",
            "correct, null",
    })
    void loginWithInvalidBody(String username, String password) throws Exception {
        whenLogin(username, password);

        thenShouldBeStatus(BAD_REQUEST);
    }

    @Test
    void register() throws Exception {
        whenRegister("Test", "Test", "+380951234567", "example@example.com", "password123");

        thenShouldBeStatus(CREATED);
        verify(authService).register(new RegistrationForm(
                "Test", "Test", "+380951234567", "example@example.com", "password123"
        ));
    }

    @ParameterizedTest
    @CsvSource(nullValues = "null", value = {
            // incorrect firstName
            //first last    phone                               email                   password
            "null,    Test, +380951234567,                      example@example.com,    password1",
            "12345, Test,   +380951234567,                      example@example.com,    password1",
            "aa,    Test,   +380951234567,                      example@example.com,    password1",
            // incorrect lastName
            "Test,  null,   +380951234567,                      example@example.com,    password1",
            "Test,  12345,  +380951234567,                      example@example.com,    password1",
            "Test,  aa,     +380951234567,                      example@example.com,    password1",
            // incorrect phone
            "Test,  Test,   null,                               example@example.com,    password1",
            "Test,  Test,   aaa,                                example@example.com,    password1",
            "Test,  Test,   380951234567,                       example@example.com,    password1",
            "Test,  Test,   38095 1234567,                      example@example.com,    password1",
            "Test,  Test,   +38095123456774187987348978937984,  example@example.com,    password1",
            // incorrect email
            "Test,  Test,   +380951234567,                      null,                   password1",
            "Test,  Test,   +380951234567,                      example@exacom,         password1",
            "Test,  Test,   +380951234567,                      exampleexacom,          password1",
            "Test,  Test,   +380951234567,                      example@g.com,          password1",
            "Test,  Test,   +380951234567,                      @g.com,                 password1",
            // incorrect password
            "Test,  Test,   +380951234567,                      example@example.com,    null",
            "Test,  Test,   +380951234567,                      example@example.com,    password",
            "Test,  Test,   +380951234567,                      example@example.com,    pa1",
            "Test,  Test,   +380951234567,                      example@example.com,    23424242"
    })
    void registerWithInvalidBody(String firstName, String lastName, String phone, String email, String password)
            throws Exception {
        whenRegister(firstName, lastName, phone, email, password);

        thenShouldBeStatus(BAD_REQUEST);
    }

    @Test
    void activateNewAccount() throws Exception {
        whenActivateNewAccount("token");

        thenShouldBeStatus(NO_CONTENT);
        verify(authService).activateNewAccountBy(new TokenForm("token"));
    }

    @Test
    void activateNewAccountWithInvalidBody() throws Exception {
        whenActivateNewAccount(null);

        thenShouldBeStatus(BAD_REQUEST);
    }

    @Test
    void restorePassword() throws Exception {
        whenRestorePassword("user@email.com");

        thenShouldBeStatus(NO_CONTENT);
        verify(authService).restore(new RestorePasswordForm("user@email.com"));
    }

    @Test
    void restorePasswordWithInvalidBody() throws Exception {
        whenRestorePassword(null);

        thenShouldBeStatus(BAD_REQUEST);
    }

    @Test
    void setNewPassword() throws Exception {
        whenSetNewPassword("token", "password1");

        thenShouldBeStatus(NO_CONTENT);
        verify(authService).setNewPassword(new SetPasswordForm(
                "token", "password1"
        ));
    }

    @ParameterizedTest
    @CsvSource(value = {
            // invalid token
            "null,                          aaaa1234",
            // invalid password
            "token,                         null",
            "token,                         aaaaaaa",
            "token,                         1234567",
            "token,                         aaa123"
    }, nullValues = "null")
    void setNewPasswordWithInvalidBody(String token, String password) throws Exception {
        whenSetNewPassword(token, password);

        thenShouldBeStatus(BAD_REQUEST);
    }

    private void givenTokenForUser(String token, String username, String password) {
        when(authService.login(username, password))
                .thenReturn(token);
        when(authDtoMapper.toTokenDto(token))
                .thenReturn(new TokenDto(token));
    }

    private void whenLogin(String username, String password) throws Exception {
        String body = loginBody(username, password);

        responseResult = mvc.perform(post("/api/auth/login")
                .contentType(APPLICATION_JSON)
                .content(body));
    }

    private void whenRegister(String firstName, String lastName, String phone, String email, String password) throws Exception {
        String body = registerBody(firstName, lastName, phone, email, password);

        responseResult = mvc.perform(post("/api/auth/register")
                .contentType(APPLICATION_JSON)
                .content(body));

    }

    private void whenActivateNewAccount(String token) throws Exception {
        String body = activateNewAccountBody(token);

        responseResult = mvc.perform(post("/api/auth/activate")
                .contentType(APPLICATION_JSON)
                .content(body));
    }

    private void whenRestorePassword(String email) throws Exception {
        String body = restorePasswordBody(email);

        responseResult = mvc.perform(post("/api/auth/restore")
                .contentType(APPLICATION_JSON)
                .content(body));
    }

    private void whenSetNewPassword(String token, String password) throws Exception {
        String body = setNewPasswordBody(token, password);

        responseResult = mvc.perform(put("/api/auth/restore/password")
                .contentType(APPLICATION_JSON)
                .content(body));
    }

    private void thenShouldBeStatus(HttpStatus status) throws Exception {
        responseResult.andExpect(status().is(status.value()));
    }

    private void thenShouldBeJsonResponse(String expectedJson) throws Exception {
        responseResult.andExpect(content().json(expectedJson, true));
    }

    private String loginBody(String username, String password)
            throws JsonProcessingException {
        HashMap<String, Object> json = new HashMap<>();
        json.put("username", username);
        json.put("password", password);
        return mapper.writeValueAsString(json);
    }

    private String registerBody(String firstName, String lastName, String phone, String email, String password)
            throws JsonProcessingException {
        HashMap<String, Object> json = new HashMap<>();
        json.put("firstName", firstName);
        json.put("lastName", lastName);
        json.put("phone", phone);
        json.put("email", email);
        json.put("password", password);
        return mapper.writeValueAsString(json);
    }

    private String activateNewAccountBody(String token) throws JsonProcessingException {
        HashMap<String, Object> json = new HashMap<>();
        json.put("token", token);
        return mapper.writeValueAsString(json);
    }

    private String restorePasswordBody(String email) throws JsonProcessingException {
        HashMap<String, Object> json = new HashMap<>();
        json.put("email", email);
        return mapper.writeValueAsString(json);
    }

    private String setNewPasswordBody(String token, String password) throws JsonProcessingException {
        HashMap<String, Object> json = new HashMap<>();
        json.put("token", token);
        json.put("password", password);
        return mapper.writeValueAsString(json);
    }
}
