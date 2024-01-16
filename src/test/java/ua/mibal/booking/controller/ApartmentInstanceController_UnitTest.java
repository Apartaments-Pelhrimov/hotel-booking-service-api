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

package ua.mibal.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ua.mibal.booking.model.dto.request.CreateApartmentInstanceDto;
import ua.mibal.booking.service.ApartmentInstanceService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@WebMvcTest(ApartmentInstanceController.class)
@TestPropertySource("classpath:application.yaml")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ApartmentInstanceController_UnitTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mvc;

    @MockBean
    private ApartmentInstanceService apartmentInstanceService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @ParameterizedTest
    @MethodSource("ua.mibal.booking.testUtils.DataGenerator#validCreateApartmentInstanceDto")
    void create(CreateApartmentInstanceDto createApartmentInstanceDto) throws Exception {
        mvc.perform(post("/api/apartments/{id}/instances", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createApartmentInstanceDto)))
                .andExpect(status().isCreated());

        verify(apartmentInstanceService, times(1))
                .create(1L, createApartmentInstanceDto);
    }

    @ParameterizedTest
    @MethodSource("ua.mibal.booking.testUtils.DataGenerator#invalidCreateApartmentInstanceDto")
    void create_should_throw_if_dto_is_invalid(CreateApartmentInstanceDto createApartmentInstanceDto) throws Exception {
        mvc.perform(post("/api/apartments/{id}/instances", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createApartmentInstanceDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(apartmentInstanceService);
    }

    @ParameterizedTest
    @CsvSource({"1", "1000000", "" + Long.MAX_VALUE, "" + Long.MIN_VALUE})
    void delete_should_delegate(Long id) throws Exception {
        mvc.perform(delete("/api/apartments/instances/{id}", id))
                .andExpect(status().isNoContent());

        verify(apartmentInstanceService, times(1))
                .delete(id);
    }
}
