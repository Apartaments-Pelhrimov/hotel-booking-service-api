/*
 * Copyright (c) 2023. Mykhailo Balakhon mailto:9mohapx9@gmail.com
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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ua.mibal.booking.model.dto.request.CreateCommentDto;
import ua.mibal.booking.service.CommentService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
@WebMvcTest(CommentController.class)
@TestPropertySource("classpath:application.yaml")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CommentController_UnitTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mvc;

    @MockBean
    private CommentService commentService;

    @BeforeEach
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
//                .apply(springSecurity())
                .build();
    }


    @ParameterizedTest
    @CsvSource({"1-1", "value", "superman2004"})
    void getAllByApartment_should_throw_if_id_path_variable_is_invalid(String id) throws Exception {
        mvc.perform(get("/api/apartments/{apartmentId}/comments", id))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(commentService);
    }

    @ParameterizedTest
    @CsvSource({"1", "1000000", "" + Long.MAX_VALUE, "" + Long.MIN_VALUE})
    void getAllByApartment_should_correct_handle_id(Long id) throws Exception {
        mvc.perform(get("/api/apartments/{apartmentId}/comments", id))
                .andExpect(status().isOk());

        verify(commentService, times(1))
                .getAllByApartment(eq(id), any());
    }


    @Disabled("Spring Authentication does not work at test environment")
    @Test
    void create_should_allow_only_ROLE_USER() throws Exception {
        mvc.perform(post("/api/apartments/{apartmentId}/comments", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateCommentDto("body", 5.))))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(commentService);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "null, null",
            "correct_body, -0.1",
            "correct_body, 5.1",
            "'      ', 0",
            "'', 0"
    }, nullValues = "null")
    void create_should_validate_CreateCommentDto(String body, Double rate) throws Exception {
        mvc.perform(post("/api/apartments/{apartmentId}/comments", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateCommentDto(body, rate))))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(commentService);
    }

    @Disabled("Spring Authentication does not work at test environment")
    @ParameterizedTest
    @CsvSource({
            "1, correct_body1, 5.0",
            "2, correct_body2, 1.0",
            "482479283, correct_body3, 0.0",
            "55, correct_body4, 4.5",
            "4893749872138478, correct_body5, 3.21",
    })
    void create_should_handle_args_to_CommentService(Long apartmentId, String body, Double rate) throws Exception {
        CreateCommentDto createCommentDto = new CreateCommentDto(body, rate);
        mvc.perform(post("/api/apartments/{apartmentId}/comments", apartmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCommentDto)))
                .andExpect(status().isOk());

        verify(commentService, times(1))
                .create(createCommentDto, any(), apartmentId);
    }
}
