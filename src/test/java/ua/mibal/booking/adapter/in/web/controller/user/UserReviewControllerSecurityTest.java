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

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ua.mibal.booking.adapter.in.web.controller.SecurityControllerTest;
import ua.mibal.booking.application.ReviewService;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.mibal.booking.adapter.in.web.security.TestSecurityJwtUtils.jwt;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@WebMvcTest(UserReviewController.class)
class UserReviewControllerSecurityTest extends SecurityControllerTest {

    @MockBean
    private ReviewService reviewService;

    @Test
    void create() throws Exception {
        mvc.perform(post("/api/apartments/{apartmentId}/reviews", "1L")
                        .with(jwt("user@email.com", "USER"))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "body": "Great apartment!",
                                  "rate": 4.9
                                }
                                """))

                .andExpect(status().isCreated());
    }

    @Test
    void createWithoutNeededAuthorities() throws Exception {
        mvc.perform(post("/api/apartments/{apartmentId}/reviews", 1L)
                        .with(jwt("user@email.com", "NOT_USER"))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "body": "Great apartment!",
                                  "rate": 4.9
                                }
                                """))

                .andExpect(status().isForbidden());
    }

    @Test
    void createWithoutAuthorization() throws Exception {
        mvc.perform(post("/api/apartments/{apartmentId}/reviews", 1L)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "body": "Great apartment!",
                                  "rate": 4.9
                                }
                                """))

                .andExpect(status().isForbidden());
    }

    @Test
    void delete() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .delete("/api/reviews/{id}", 2L)
                        .with(jwt("user@email.com", "USER")))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteWithoutNeededAuthorities() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .delete("/api/reviews/{id}", 2L)
                        .with(jwt("user@email.com", "NOT_USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteWithoutAuthorization() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .delete("/api/reviews/{id}", 2L))
                .andExpect(status().isForbidden());
    }
}
