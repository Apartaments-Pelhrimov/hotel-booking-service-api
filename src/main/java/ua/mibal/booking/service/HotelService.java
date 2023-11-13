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
import ua.mibal.booking.mapper.HotelMapper;
import ua.mibal.booking.model.dto.HotelDto;
import ua.mibal.booking.model.dto.HotelSearchDto;
import ua.mibal.booking.model.search.Request;
import ua.mibal.booking.repository.HotelRepository;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@RequiredArgsConstructor
@Service
public class HotelService {
    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;

    public Page<HotelSearchDto> getAllBySearchRequest(Request request, Pageable pageable) {
        return hotelRepository.findAllByQuery(request, pageable)
                .map(hotel -> hotelMapper
                        .toDto(hotel, null));
    }

    public HotelDto getOne(Long id) {
        return hotelRepository.findByIdFetchPhotos(id)
                .map(hotelMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Entity Hotel by id=" + id + " not found"));
    }

    public Page<HotelSearchDto> getAll(Pageable pageable) {
        return hotelRepository.findAll(pageable)
                .map(hotel -> hotelMapper
                        .toDto(hotel, null));
    }
}
