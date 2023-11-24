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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.mibal.booking.model.dto.response.ApartmentDto;
import ua.mibal.booking.model.dto.search.ApartmentCardDto;
import ua.mibal.booking.service.ApartmentService;

import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/hotels")
public class ApartmentController {
    private final ApartmentService apartmentService;

    @GetMapping("/apartments/{id}")
    public ApartmentDto getOne(@PathVariable Long id) {
        return apartmentService.getOne(id);
    }

    @GetMapping("/apartments")
    public List<ApartmentCardDto> getAll() {
        return apartmentService.getAll();
    }
}
