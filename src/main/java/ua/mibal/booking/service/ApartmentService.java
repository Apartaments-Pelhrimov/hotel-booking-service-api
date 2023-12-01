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

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.mibal.booking.model.dto.response.ApartmentTypeCardDto;
import ua.mibal.booking.model.dto.response.ApartmentTypeDto;
import ua.mibal.booking.model.entity.ApartmentType;
import ua.mibal.booking.model.exception.entity.ApartmentNotFoundException;
import ua.mibal.booking.model.mapper.ApartmentMapper;
import ua.mibal.booking.repository.ApartmentTypeRepository;

import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class ApartmentService {
    private final ApartmentTypeRepository apartmentTypeRepository;
    private final ApartmentMapper apartmentMapper;

    @Transactional(readOnly = true) // for LAZY Apartment.beds fetch
    public ApartmentTypeDto getOneDto(Long id) {
        return apartmentTypeRepository.findByIdFetchPhotos(id)
                .map(apartmentMapper::toDto)
                .orElseThrow(() -> new ApartmentNotFoundException(id));
    }

    public ApartmentType getOne(Long id) {
        return apartmentTypeRepository.findByIdFetchPhotos(id)
                .orElseThrow(() -> new ApartmentNotFoundException(id));
    }

    @Transactional(readOnly = true) // for LAZY Apartment.beds fetch
    public List<ApartmentTypeCardDto> getAll() {
        return apartmentTypeRepository.findAllFetchPhotos()
                .stream()
                .map(apartmentMapper::toCardDto)
                .toList();
    }
}
