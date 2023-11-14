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
import ua.mibal.booking.model.mapper.ApartmentMapper;
import ua.mibal.booking.model.dto.response.ApartmentDto;
import ua.mibal.booking.model.dto.search.ApartmentSearchDto;
import ua.mibal.booking.model.dto.response.FreeApartmentDto;
import ua.mibal.booking.model.search.DateRangeRequest;
import ua.mibal.booking.model.search.Request;
import ua.mibal.booking.repository.ApartmentRepository;

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
    public Page<ApartmentSearchDto> getAllInHotelBySearchRequest(Long hotelId, Request request, Pageable pageable) {
        return apartmentRepository.findAllInHotelByQuery(hotelId, request, pageable)
                .map(apartmentMapper::toSearchDto);
    }

    @Transactional(readOnly = true) // for LAZY Apartment.beds fetch
    public ApartmentDto getOne(Long id) {
        return apartmentRepository.findByIdFetchPhotosHotel(id)
                .map(apartmentMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Entity Apartment by id=" + id + " not found"));
    }

    public FreeApartmentDto isFree(Long id, DateRangeRequest request) {
        return apartmentRepository
                .isFreeForRangeById(id, request.getFrom(), request.getTo())
                .map(apartmentMapper::toFreeDto)
                .orElseThrow(() -> new EntityNotFoundException("Entity Apartment by id=" + id + " not found"));
    }

    @Transactional(readOnly = true) // for LAZY Apartment.beds fetch
    public Page<ApartmentSearchDto> getAllByQuery(String query, Pageable pageable) {
        return apartmentRepository.findAllByNameOrCity(query, pageable)
                .map(apartmentMapper::toSearchDto);
    }

    @Transactional(readOnly = true) // for LAZY Apartment.beds fetch
    public Page<ApartmentSearchDto> getAllInHotelByQuery(Long hotelId, String query, Pageable pageable) {
        return apartmentRepository.findAllInHotelByNameOrCity(hotelId, query, pageable)
                .map(apartmentMapper::toSearchDto);
    }
}
