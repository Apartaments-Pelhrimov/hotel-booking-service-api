package ua.mibal.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ua.mibal.booking.model.dto.auth.AuthResponseDto;
import ua.mibal.booking.model.dto.auth.RegistrationDto;
import ua.mibal.booking.service.AuthService;
import ua.mibal.booking.testUtils.RegistrationDtoArgumentConverter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
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

    @BeforeEach
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void login_should_handle_call_AuthService() throws Exception {
        AuthResponseDto expectedResponse = new AuthResponseDto("first", "last", "token");
        when(authService.token(any()))
                .thenReturn(expectedResponse);

        mvc.perform(post("/api/auth/login")
                        .with(httpBasic("username", "password")))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
        verify(authService, times(1))
                .token(any());
    }

    @ParameterizedTest(name = "[{index}] {arguments}")
    @CsvSource(nullValues = "null", value = {
            "null",
            // firstName
            "12345 Test +380951234567 example@example.com password1",
            "aa Test +380951234567 example@example.com password1",
            // lastName
            "Test 12345 +380951234567 example@example.com password1",
            "Test aa +380951234567 example@example.com password1",
            // phone
            "Test Test aaa example@example.com password1",
            "Test Test 380951234567 example@example.com password1",
            "Test Test 38095 1234567 example@example.com password1",
            "Test Test +38095123456774187987348978937984 example@example.com password1",
            // mail
            "Test Test +380951234567 example@exacom password1",
            "Test Test +380951234567 exampleexacom password1",
            "Test Test +380951234567 example@g.com password1",
            "Test Test +380951234567 @g.com password1",
            // password
            "Test Test +380951234567 example@exacom password",
            "Test Test +380951234567 example@exacom pa1",
            "Test Test +380951234567 example@exacom 23424242" // FIXME
    })
    void register_should_throw_ValidationException_while_pass_incorrect_RegistrationDto(@ConvertWith(RegistrationDtoArgumentConverter.class)
                                                                                        RegistrationDto registrationDto) throws Exception {
        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @CsvSource("Test Test +380951234567 example@example.com password123")
    void register_should_accept_correct_RegistrationDto(@ConvertWith(RegistrationDtoArgumentConverter.class)
                                                        RegistrationDto registrationDto) throws Exception {
        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isOk());
        verify(authService, times(1))
                .register(registrationDto);
    }
}
