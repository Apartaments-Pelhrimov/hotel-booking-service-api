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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.mibal.booking.model.dto.ApartmentDto;
import ua.mibal.booking.model.dto.ApartmentSearchDto;
import ua.mibal.booking.model.dto.FreeApartmentDto;
import ua.mibal.booking.model.search.DateRangeRequest;
import ua.mibal.booking.model.search.Request;
import ua.mibal.booking.service.ApartmentService;

import java.util.Optional;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/hotels")
public class ApartmentController {
    private final ApartmentService apartmentService;

    @GetMapping("/{hotelId}/apartments/search")
    public Page<ApartmentSearchDto> getAllInHotelByQuery(@PathVariable Long hotelId,
                                                         @Valid Request request,
                                                         Pageable pageable) {
        return apartmentService.getAllInHotelBySearchRequest(hotelId, request, pageable);
    }

    @GetMapping("/apartments")
    public Page<ApartmentSearchDto> getAll(@RequestParam(required = false, defaultValue = "") String query,
                                           @RequestParam(required = false) Long hotelId,
                                           Pageable pageable) {
        return Optional.ofNullable(hotelId)
                .map(hId -> apartmentService.getAllInHotelByQuery(hId, query, pageable))
                .orElseGet(() -> apartmentService.getAllByQuery(query, pageable));
    }

    @GetMapping("/apartments/{id}")
    public ApartmentDto getOne(@PathVariable Long id) {
        return apartmentService.getOne(id);
    }

    @GetMapping("/apartments/{id}/free")
    public FreeApartmentDto isFree(@PathVariable Long id, @Valid DateRangeRequest request) {
        return apartmentService.isFree(id, request);
    }
}
