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

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.mibal.booking.adapter.in.web.mapper.ReviewDtoMapper;
import ua.mibal.booking.adapter.in.web.model.ReviewDto;
import ua.mibal.booking.application.ReviewService;
import ua.mibal.booking.domain.Review;
import ua.mibal.booking.domain.id.ApartmentId;

import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewDtoMapper reviewDtoMapper;

    @GetMapping("/reviews/latest")
    public List<ReviewDto> getAllLatest(Pageable pageable) {
        List<Review> reviews = reviewService.getAllLatest(pageable);
        return reviewDtoMapper.toDtos(reviews);
    }

    @GetMapping("/apartments/{apartmentId}/reviews")
    public List<ReviewDto> getAllByApartment(@PathVariable String apartmentId,
                                             Pageable pageable) {
        List<Review> reviews = reviewService.getAllByApartment(
                new ApartmentId(apartmentId), pageable);
        return reviewDtoMapper.toDtos(reviews);
    }
}
