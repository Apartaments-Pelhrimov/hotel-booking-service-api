/*
 * Copyright (c) 2023. Mykhailo Balakhon, mailto:9mohapx9@gmail.com
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

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.mibal.booking.model.dto.response.CommentDto;
import ua.mibal.booking.service.CommentService;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/hotels")
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/{hotelId}/comments")
    public Page<CommentDto> getCommentsForHotel(@PathVariable Long hotelId, Pageable pageable) {
        return commentService.getCommentsInHotel(hotelId, pageable);
    }

    @GetMapping("apartments/{apartmentId}/comments")
    public Page<CommentDto> getCommentsForApartment(@PathVariable Long apartmentId, Pageable pageable) {
        return commentService.getCommentsInApartment(apartmentId, pageable);
    }
}
