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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ua.mibal.booking.adapter.in.web.mapper.ApartmentDtoMapper;
import ua.mibal.booking.application.ApartmentService;
import ua.mibal.booking.application.model.CreateApartmentForm;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@WebMvcTest(ApartmentController.class)
@TestPropertySource("classpath:application.yaml")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ApartmentController_UnitTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mvc;

    @MockBean
    private ApartmentService apartmentService;
    @MockBean
    private ApartmentDtoMapper apartmentDtoMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @ParameterizedTest
    @CsvSource({"1-1", "value", "superman2004"})
    void getOne_should_throw_exception_if_id_path_variable_is_illegal(String id) throws Exception {
        mvc.perform(get("/api/apartments/{id}", id))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(apartmentService);
    }

    @ParameterizedTest
    @CsvSource({"1", "1000000", "" + Long.MAX_VALUE, "" + Long.MIN_VALUE})
    void getOne_should_handle_id_correct(Long id) throws Exception {
        mvc.perform(get("/api/apartments/{id}", id))
                .andExpect(status().isOk());

        verify(apartmentService, times(1))
                .getOneFetchPhotosBeds(id);
    }

    @ParameterizedTest
    @MethodSource("ua.mibal.test.util.DataGenerator#validCreateApartmentForm")
    void create(CreateApartmentForm createApartmentForm) throws Exception {
        mvc.perform(post("/api/apartments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createApartmentForm)))
                .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @MethodSource("ua.mibal.test.util.DataGenerator#invalidApartmentDto")
    void create_should_throw_if_dto_is_invalid(CreateApartmentForm createApartmentForm) throws Exception {
        mvc.perform(post("/api/apartments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createApartmentForm)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @CsvSource({"1", "10000", "489374981"})
    void delete(Long id) throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/api/apartments/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
