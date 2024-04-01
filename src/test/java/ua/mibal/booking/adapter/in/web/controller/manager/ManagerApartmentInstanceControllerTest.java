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

package ua.mibal.booking.adapter.in.web.controller.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import ua.mibal.booking.adapter.in.web.controller.ControllerTest;
import ua.mibal.booking.application.ApartmentInstanceService;
import ua.mibal.booking.application.model.CreateApartmentInstanceForm;
import ua.mibal.booking.domain.id.ApartmentId;

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
@WebMvcTest(ManagerApartmentInstanceController.class)
class ManagerApartmentInstanceControllerTest extends ControllerTest {

    @MockBean
    private ApartmentInstanceService apartmentInstanceService;

    @Autowired
    private ObjectMapper objectMapper;

    @ParameterizedTest
    @MethodSource("ua.mibal.test.util.DataGenerator#validCreateApartmentInstanceForms")
    void create(CreateApartmentInstanceForm form) throws Exception {
        ApartmentId apartmentId = new ApartmentId("apartment-id");

        mvc.perform(post("/api/apartments/{id}/instances", "apartment-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isCreated());

        form.setApartmentId(apartmentId);

        verify(apartmentInstanceService, times(1)).create(form);
    }

    @ParameterizedTest
    @MethodSource("ua.mibal.test.util.DataGenerator#invalidCreateApartmentInstanceForms")
    void create_should_throw_if_dto_is_invalid(CreateApartmentInstanceForm form) throws Exception {
        mvc.perform(post("/api/apartments/{id}/instances", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(apartmentInstanceService);
    }

    @ParameterizedTest
    @CsvSource({"1", "1000000", "" + Long.MAX_VALUE, "" + Long.MIN_VALUE})
    void delete_should_delegate(Long id) throws Exception {
        mvc.perform(delete("/api/apartments/instances/{id}", id))
                .andExpect(status().isNoContent());

        verify(apartmentInstanceService, times(1)).delete(id);
    }
}
