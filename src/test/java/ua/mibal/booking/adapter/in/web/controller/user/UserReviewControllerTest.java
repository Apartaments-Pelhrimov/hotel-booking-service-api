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

package ua.mibal.booking.adapter.in.web.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ua.mibal.booking.adapter.in.web.controller.ControllerTest;
import ua.mibal.booking.application.ReviewService;
import ua.mibal.booking.application.model.CreateReviewForm;
import ua.mibal.booking.domain.id.ApartmentId;

import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.mibal.booking.adapter.in.web.security.TestSecurityJwtUtils.jwt;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@WebMvcTest(UserReviewController.class)
class UserReviewControllerTest extends ControllerTest {
    private static final String USER_USERNAME = "user@email.com";
    private static final String USER_ROLE = "USER";
    private static final ApartmentId APARTMENT_ID = new ApartmentId("1L");
    private static final Long REVIEW_ID = 2L;

    @MockBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper mapper;

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
    void create(String body, Double rate) throws Exception {
        whenCreateThenShouldBeStatus(body, rate, HttpStatus.CREATED);

        verify(reviewService).create(new CreateReviewForm(
                body, rate, APARTMENT_ID, USER_USERNAME
        ));
    }

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
        whenCreateThenShouldBeStatus(body, rate, BAD_REQUEST);

        verify(reviewService, never()).create(any());
    }

    @Test
    void delete() throws Exception {
        whenDeleteThenShouldBeStatus(NO_CONTENT);

        verify(reviewService).delete(REVIEW_ID, USER_USERNAME);
    }

    private void whenDeleteThenShouldBeStatus(HttpStatus status) throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .delete("/api/reviews/{id}", REVIEW_ID)
                        .with(jwt(USER_USERNAME, USER_ROLE)))
                .andExpect(status().is(status.value()));
    }

    private void whenCreateThenShouldBeStatus(String body, Double rate, HttpStatus status) throws Exception {
        String json = createRequestJson(body, rate);

        mvc.perform(post("/api/apartments/{apartmentId}/reviews", APARTMENT_ID.value())
                        .with(jwt(USER_USERNAME, USER_ROLE))
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(status.value()));
    }

    private String createRequestJson(String body, Double rate) throws JsonProcessingException {
        HashMap<String, Object> json = new HashMap<>();
        json.put("body", body);
        json.put("rate", rate);
        return mapper.writeValueAsString(json);
    }
}
