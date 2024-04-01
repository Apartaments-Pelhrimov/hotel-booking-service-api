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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.mibal.booking.adapter.in.web.security.annotation.UserAllowed;
import ua.mibal.booking.application.ReviewService;
import ua.mibal.booking.application.model.CreateReviewForm;
import ua.mibal.booking.domain.id.ApartmentId;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@UserAllowed
@RestController
@RequestMapping("/api")
public class UserReviewController {
    private final ReviewService reviewService;

    @PostMapping("/apartments/{apartmentId}/reviews")
    @ResponseStatus(CREATED)
    public void create(@PathVariable String apartmentId,
                       @Valid @RequestBody CreateReviewForm form,
                       Authentication authentication) {
        form.setApartmentId(new ApartmentId(apartmentId));
        form.setUserEmail(authentication.getName());
        reviewService.create(form);
    }

    @DeleteMapping("/reviews/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable Long id,
                       Authentication authentication) {
        reviewService.delete(id, authentication.getName());
    }
}