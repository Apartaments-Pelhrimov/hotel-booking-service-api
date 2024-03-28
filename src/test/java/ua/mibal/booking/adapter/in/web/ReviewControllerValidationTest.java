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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ua.mibal.booking.adapter.in.web.mapper.ReviewDtoMapper;
import ua.mibal.booking.application.ReviewService;
import ua.mibal.booking.application.model.CreateReviewForm;
import ua.mibal.booking.domain.id.ApartmentId;

import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.mibal.booking.adapter.in.web.security.TestSecurityJwtUtils.jwt;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@WebMvcTest(ReviewController.class)
public class ReviewControllerValidationTest extends ControllerValidationTest {
    private static final String APARTMENT_ID = "apartment-id";
    private static final String USER_USERNAME = "user@email.com";

    @MockBean
    private ReviewService reviewService;
    @MockBean
    private ReviewDtoMapper reviewDtoMapper;

    @Autowired
    private ObjectMapper mapper;

    @ParameterizedTest
    @CsvSource({
            //    Invalid rate
            // body         rate
            "b,             10.1",
            "body,          -0.1",

            //    Invalid body
            // body         rate
            "'    ',        0",
            "'',            10",
    })
    void createWithInvalidBody(String body, Double rate) throws Exception {
        whenCreateThenShouldBeStatus(body, rate, 400);

        verify(reviewService, never()).create(any());
    }

    @ParameterizedTest
    @CsvSource({
            // body         rate
            "bo,            0",
            "body,          0",
            "body,          1.1",
            "body,          1.4289174892174928",
            "body,          9.999999999999999",
            "body,          10",
    })
    void createWithValidBody(String body, Double rate) throws Exception {
        whenCreateThenShouldBeStatus(body, rate, 201);

        verify(reviewService).create(new CreateReviewForm(
                body, rate, new ApartmentId(APARTMENT_ID), USER_USERNAME
        ));
    }

    private void whenCreateThenShouldBeStatus(String body, Double rate, int status) throws Exception {
        String json = createRequestJson(body, rate);

        mvc.perform(post("/api/apartments/{apartmentId}/reviews", APARTMENT_ID)
                        .with(jwt(USER_USERNAME, "USER"))
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(status));
    }

    private String createRequestJson(String body, Double rate) throws JsonProcessingException {
        HashMap<String, Object> json = new HashMap<>();
        json.put("body", body);
        json.put("rate", rate);
        return mapper.writeValueAsString(json);
    }
}
