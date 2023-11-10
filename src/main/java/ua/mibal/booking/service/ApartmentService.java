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

package ua.mibal.booking.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.mibal.booking.mapper.ApartmentMapper;
import ua.mibal.booking.model.dto.ApartmentDto;
import ua.mibal.booking.model.dto.ApartmentSearchDto;
import ua.mibal.booking.model.dto.FreeApartmentDto;
import ua.mibal.booking.model.search.Request;
import ua.mibal.booking.repository.ApartmentRepository;

import java.time.LocalDate;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@RequiredArgsConstructor
@Service
public class ApartmentService {
    private final ApartmentRepository apartmentRepository;
    private final ApartmentMapper apartmentMapper;

    @Transactional(readOnly = true) // for LAZY Apartment.beds fetch
    public Page<ApartmentSearchDto> getAllInHotel(Long hotelId, Request request, Pageable pageable) {
        return apartmentRepository.findAllInHotel(hotelId, request, pageable)
                .map(apartment -> apartmentMapper.toSearchDto(apartment, null));
    }

    @Transactional(readOnly = true) // for LAZY Apartment.beds fetch
    public ApartmentDto getOne(Long id) {
        return apartmentRepository.findByIdFetchPhotosHotel(id)
                .map(apartment -> apartmentMapper.toDto(apartment, null))
                .orElseThrow(() -> new EntityNotFoundException("Entity Apartment by id=" + id + " not found"));
    }

    public FreeApartmentDto isFree(Long id, LocalDate from, LocalDate to) {
        Boolean free = apartmentRepository.isFreeForRangeById(id, from, to);
        return apartmentMapper.toFreeDto(free);
    }
}
