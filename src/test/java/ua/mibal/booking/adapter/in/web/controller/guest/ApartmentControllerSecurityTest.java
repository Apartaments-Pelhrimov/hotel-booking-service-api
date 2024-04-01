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

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ua.mibal.booking.adapter.in.web.controller.SecurityControllerTest;
import ua.mibal.booking.adapter.in.web.mapper.ApartmentDtoMapper;
import ua.mibal.booking.application.ApartmentService;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@WebMvcTest(ApartmentController.class)
class ApartmentControllerSecurityTest extends SecurityControllerTest {

    @MockBean
    private ApartmentService apartmentService;
    @MockBean
    private ApartmentDtoMapper apartmentDtoMapper;

    @Test
    void getPropositionsWithoutAuthorization() throws Exception {
        mvc.perform(get("/api/apartments/propositions"))
                .andExpect(status().isOk());
    }

    @Test
    void getOneWithoutAuthorization() throws Exception {
        mvc.perform(get("/api/apartments/{id}", "amazing-apartment-id"))
                .andExpect(status().isOk());
    }

    @Test
    void searchWithoutAuthorization() throws Exception {
        mvc.perform(get("/api/apartments")
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }
}
