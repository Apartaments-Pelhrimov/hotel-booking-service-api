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

package ua.mibal.booking.controller.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.WebApplicationContext;
import ua.mibal.booking.controller.ApartmentController;
import ua.mibal.booking.controller.AuthController;
import ua.mibal.booking.controller.advice.model.ApiError;
import ua.mibal.booking.model.dto.auth.RegistrationDto;
import ua.mibal.booking.model.exception.EmailAlreadyExistsException;
import ua.mibal.booking.model.exception.entity.ApartmentNotFoundException;
import ua.mibal.booking.model.exception.service.CostCalculationServiceException;
import ua.mibal.booking.service.ApartmentService;
import ua.mibal.booking.service.AuthService;
import ua.mibal.booking.testUtils.DataGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@WebMvcTest({
        ExceptionHandlerAdvice.class,
        ApartmentController.class,
        AuthController.class,
})
@TestPropertySource("classpath:application.yaml")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ExceptionHandlerAdvice_UnitTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mvc;

    @MockBean
    private ApartmentService apartmentService;
    @MockBean
    private AuthService authService;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void handleValidationException() throws Exception {
        RegistrationDto registrationDto = DataGenerator.invalidRegistrationDto();
        ApiError expectedError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                MethodArgumentNotValidException.class.getSimpleName(),
                ""
        );

        String responseContent = mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();


        ApiError responseError = objectMapper.readValue(responseContent, ApiError.class);
        assertEquals(expectedError.getStatus(), responseError.getStatus());
        assertEquals(expectedError.getError(), responseError.getError());

        assertTrue(responseError.getMessage().contains(registrationDto.password()));
        assertTrue(responseError.getMessage().contains(registrationDto.email()));
        assertTrue(responseError.getMessage().contains(registrationDto.phone()));
    }

    @Test
    void handleNotFoundException_at_ApartmentController_delete() throws Exception {
        Long id = 1L;
        ApiError expectedError = ApiError.ofException(
                HttpStatus.NOT_FOUND, new ApartmentNotFoundException(id)
        );
        doThrow(new ApartmentNotFoundException(id)).when(apartmentService).delete(id);


        String responseContent = mvc.perform(delete("/api/apartments/{id}", id))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();


        ApiError responseError = objectMapper.readValue(responseContent, ApiError.class);
        assertEquals(expectedError.getStatus(), responseError.getStatus());
        assertEquals(expectedError.getError(), responseError.getError());
        assertEquals(expectedError.getMessage(), responseError.getMessage());
    }

    @Test
    void handleBadRequestException_at_AuthService_register() throws Exception {
        String email = "user@email.com";
        RegistrationDto registrationDto = DataGenerator.testRegistrationDto(email);
        ApiError expectedError = ApiError.ofException(
                HttpStatus.BAD_REQUEST, new EmailAlreadyExistsException(email)
        );
        doThrow(new EmailAlreadyExistsException(email))
                .when(authService).register(registrationDto);


        String responseContent = mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();


        ApiError responseError = objectMapper.readValue(responseContent, ApiError.class);
        assertEquals(expectedError.getStatus(), responseError.getStatus());
        assertEquals(expectedError.getError(), responseError.getError());
        assertEquals(expectedError.getMessage(), responseError.getMessage());
    }

    @Test
    void handleAccessDeniedException() {
        // TODO fix security tests
    }

    @Test
    void handleInternalServerException() throws Exception {
        Long id = 1L;
        ApiError expectedError = ApiError.ofException(
                HttpStatus.INTERNAL_SERVER_ERROR, new CostCalculationServiceException("test_message")
        );
        doThrow(new CostCalculationServiceException("test_message")).when(apartmentService).delete(id);


        String responseContent = mvc.perform(delete("/api/apartments/{id}", id))
                .andExpect(status().isInternalServerError())
                .andReturn().getResponse().getContentAsString();


        ApiError responseError = objectMapper.readValue(responseContent, ApiError.class);
        assertEquals(expectedError.getStatus(), responseError.getStatus());
        assertEquals(expectedError.getError(), responseError.getError());
        assertEquals(expectedError.getMessage(), responseError.getMessage());
    }
}

