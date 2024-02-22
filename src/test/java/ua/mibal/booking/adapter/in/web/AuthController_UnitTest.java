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

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ua.mibal.booking.adapter.in.web.mapper.AuthDtoMapper;
import ua.mibal.booking.adapter.in.web.model.LoginDto;
import ua.mibal.booking.adapter.in.web.model.NewPasswordDto;
import ua.mibal.booking.adapter.in.web.model.TokenDto;
import ua.mibal.booking.application.AuthService;
import ua.mibal.booking.application.dto.RegistrationForm;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@WebMvcTest(AuthController.class)
@TestPropertySource("classpath:application.yaml")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AuthController_UnitTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mvc;

    @MockBean
    private AuthService authService;
    @MockBean
    private AuthDtoMapper authDtoMapper;

    @BeforeEach
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @ParameterizedTest
    @InstancioSource
    void login(String username, String password, String token) throws Exception {
        when(authService.login(username, password))
                .thenReturn(token);
        when(authDtoMapper.toTokenDto(token))
                .thenReturn(new TokenDto(token));

        mvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginDto(username, password))))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new TokenDto(token))));
    }

    @ParameterizedTest
    @CsvSource(nullValues = "null", value = {
            "null,    null",
            "null,    correct",
            "correct, null",
    })
    void login_should_return_BAD_REQUEST(String username, String password) throws Exception {
        LoginDto incorrectLoginDto = new LoginDto(username, password);

        mvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incorrectLoginDto)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @CsvSource(nullValues = "null", value = {
            // incorrect firstName
            //first last    phone                               email                   password
            "12345, Test,   +380951234567,                      example@example.com,    password1",
            "aa,    Test,   +380951234567,                      example@example.com,    password1",
            // incorrect lastName
            "Test,  12345,  +380951234567,                      example@example.com,    password1",
            "Test,  aa,     +380951234567,                      example@example.com,    password1",
            // incorrect phone
            "Test,  Test,   aaa,                                example@example.com,    password1",
            "Test,  Test,   380951234567,                       example@example.com,    password1",
            "Test,  Test,   38095 1234567,                      example@example.com,    password1",
            "Test,  Test,   +38095123456774187987348978937984,  example@example.com,    password1",
            // incorrect mail
            "Test,  Test,   +380951234567,                      example@exacom,         password1",
            "Test,  Test,   +380951234567,                      exampleexacom,          password1",
            "Test,  Test,   +380951234567,                      example@g.com,          password1",
            "Test,  Test,   +380951234567,                      @g.com,                 password1",
            // incorrect password
            "Test,  Test,   +380951234567,                      example@example.com,    password",
            "Test,  Test,   +380951234567,                      example@example.com,    pa1",
            "Test,  Test,   +380951234567,                      example@example.com,    23424242"
    })
    void register_should_throw_ValidationException_while_pass_incorrect_RegistrationDto(
            String firstName, String lastName, String phone, String email, String password) throws Exception {
        mvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegistrationForm(firstName, lastName, phone, email, password))))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @ParameterizedTest
    @CsvSource(
            //first last    phone           email                   password
            "Test,  Test,   +380951234567,  example@example.com,    password123")
    void register_should_accept_correct_RegistrationDto(
            String firstName, String lastName, String phone, String email, String password) throws Exception {
        RegistrationForm registrationForm = new RegistrationForm(firstName, lastName, phone, email, password);

        mvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationForm)))
                .andExpect(status().isCreated());

        verify(authService, times(1))
                .register(registrationForm);
    }

    @ParameterizedTest
    @InstancioSource
    void activateNewAccount_should_delegate_token_to_AuthService(String token) throws Exception {
        mvc.perform(post("/api/auth/activate")
                        .param("token", token))
                .andExpect(status().isNoContent());

        verify(authService, times(1))
                .activateNewAccountBy(token);
    }

    @Test
    void activateNewAccount_should_throw_exception_if_token_was_not_passed() throws Exception {
        mvc.perform(post("/api/auth/activate"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @ParameterizedTest
    @InstancioSource
    void forgetPassword_should_delegate_email_to_AuthService(String email) throws Exception {
        mvc.perform(get("/api/auth/forget")
                        .param("email", email))
                .andExpect(status().isNoContent());

        verify(authService, times(1))
                .restore(email);
    }

    @Test
    void forgetPassword_should_throw_exception_if_token_was_not_passed() throws Exception {
        mvc.perform(get("/api/auth/forget"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void setNewPassword_should_delegate_params_to_AuthService() throws Exception {
        String newPass = "password1";
        String token = "token";

        mvc.perform(put("/api/auth/forget/password")
                        .param("token", token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new NewPasswordDto(newPass))))
                .andExpect(status().isNoContent());

        verify(authService, times(1))
                .setNewPassword(token, newPass);
    }

    @Test
    void setNewPassword_should_throw_exception_if_token_was_not_passed() throws Exception {
        mvc.perform(put("/api/auth/forget/password"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void setNewPassword_should_throw_exception_if_request_body_is_empty() throws Exception {
        mvc.perform(put("/api/auth/forget/password")
                        .param("token", "some_token"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "null,                          null",
            "token,                         null",
            "null,                          aaaa1234",
            "token,                         aaaaaaa",
            "27kRW2FUvh$4zghVEF8GO0uJ=~e=A, 1234567",
            "token,                         aaa123"
    }, nullValues = "null")
    void setNewPassword_should_throw_ValidationException_while_pass_incorrect_ForgetPasswordDto(String token, String password) throws Exception {
        mvc.perform(put("/api/auth/forget/password")
                        .param("token", token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new NewPasswordDto(password))))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }
}
