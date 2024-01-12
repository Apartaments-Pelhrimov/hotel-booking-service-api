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

package ua.mibal.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.mibal.booking.model.dto.request.CreateApartmentInstanceDto;
import ua.mibal.booking.model.entity.ApartmentInstance;
import ua.mibal.booking.model.exception.ApartmentIsNotAvialableForReservation;
import ua.mibal.booking.model.exception.entity.ApartmentInstanceNotFoundException;
import ua.mibal.booking.model.exception.entity.ApartmentNotFoundException;
import ua.mibal.booking.model.mapper.ApartmentInstanceMapper;
import ua.mibal.booking.model.request.ReservationFormRequest;
import ua.mibal.booking.repository.ApartmentInstanceRepository;
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
public class ApartmentInstanceService {
    private final ApartmentInstanceRepository apartmentInstanceRepository;
    private final ApartmentRepository apartmentRepository;
    private final DateTimeUtils dateTimeUtils;
    private final BookingComReservationService bookingComReservationService;
    private final ApartmentInstanceMapper apartmentInstanceMapper;

    public ApartmentInstance getFreeByApartmentId(Long apartmentId, ReservationFormRequest request) {
        LocalDateTime from = dateTimeUtils.reserveFrom(request.from());
        LocalDateTime to = dateTimeUtils.reserveTo(request.to());
        List<ApartmentInstance> apartments = apartmentInstanceRepository
                .findFreeByApartmentIdAndDates(apartmentId, from, to, request.people())
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

    public void addToApartment(Long apartmentId, CreateApartmentInstanceDto createApartmentInstanceDto) {
        validateApartmentExists(apartmentId);
        ApartmentInstance instance = apartmentInstanceMapper.toEntity(createApartmentInstanceDto);
        instance.setApartment(apartmentRepository.getReferenceById(apartmentId));
        apartmentInstanceRepository.save(instance);
    }

    public void delete(Long id) {
        validateApartmentInstanceExists(id);
        apartmentInstanceRepository.deleteById(id);
    }

    private void validateApartmentExists(Long id) {
        if (!apartmentRepository.existsById(id)) {
            throw new ApartmentNotFoundException(id);
        }
    }

    private void validateApartmentInstanceExists(Long id) {
        if (!apartmentInstanceRepository.existsById(id)) {
            throw new ApartmentInstanceNotFoundException(id);
        }
    }
}
