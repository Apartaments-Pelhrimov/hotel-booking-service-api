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
import ua.mibal.booking.model.dto.response.ApartmentCardDto;
import ua.mibal.booking.model.dto.response.ApartmentDto;
import ua.mibal.booking.model.entity.Apartment;
import ua.mibal.booking.model.entity.ApartmentInstance;
import ua.mibal.booking.model.exception.ApartmentIsNotAvialableForReservation;
import ua.mibal.booking.model.exception.entity.ApartmentNotFoundException;
import ua.mibal.booking.model.mapper.ApartmentMapper;
import ua.mibal.booking.model.request.ReservationFormRequest;
import ua.mibal.booking.repository.ApartmentRepository;
import ua.mibal.booking.service.util.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class ApartmentService {
    private final ApartmentRepository apartmentRepository;
    private final ApartmentMapper apartmentMapper;
    private final DateTimeUtils dateTimeUtils;
    private final BookingComReservationService bookingComReservationService;

    @Transactional(readOnly = true) // for LAZY Apartment.beds fetch
    public ApartmentDto getOneDto(Long id) {
        return apartmentRepository.findByIdFetchPhotos(id)
                .map(apartmentMapper::toDto)
                .orElseThrow(() -> new ApartmentNotFoundException(id));
    }

    public Apartment getOne(Long id) {
        return apartmentRepository.findByIdFetchPhotos(id)
                .orElseThrow(() -> new ApartmentNotFoundException(id));
    }

    @Transactional(readOnly = true) // for LAZY Apartment.beds fetch
    public List<ApartmentCardDto> getAll() {
        return apartmentRepository.findAllFetchPhotos()
                .stream()
                .map(apartmentMapper::toCardDto)
                .toList();
    }

    public ApartmentInstance getFreeApartmentInstanceByApartmentId(Long apartmentId, ReservationFormRequest request) {
        LocalDateTime from = dateTimeUtils.reserveFrom(request.from());
        LocalDateTime to = dateTimeUtils.reserveTo(request.to());
        List<ApartmentInstance> apartments = apartmentRepository
                .findFreeApartmentInstanceByApartmentIdAndDates(apartmentId, from, to, request.people())
                .stream()
                .filter(in -> bookingComReservationService.isFree(in, from, to))
                .toList();
        return selectMostSuitableApartmentInstance(apartments, apartmentId, from, to);
    }

    private ApartmentInstance selectMostSuitableApartmentInstance(List<ApartmentInstance> apartments,
                                                                  Long apartmentId,
                                                                  LocalDateTime from,
                                                                  LocalDateTime to) {
        if (apartments.isEmpty()) throw new ApartmentIsNotAvialableForReservation(from, to, apartmentId);
        if (apartments.size() == 1) return apartments.get(0);
        // TODO implement logic
        return apartments.get(0);
    }
}
