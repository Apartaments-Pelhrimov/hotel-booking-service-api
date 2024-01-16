/*
 * Copyright (c) 2023. Mykhailo Balakhon mailto:9mohapx9@gmail.com
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.mibal.booking.model.dto.request.ReservationRejectingFormDto;
import ua.mibal.booking.model.dto.response.ReservationDto;
import ua.mibal.booking.model.entity.Apartment;
import ua.mibal.booking.model.entity.ApartmentInstance;
import ua.mibal.booking.model.entity.Reservation;
import ua.mibal.booking.model.entity.User;
import ua.mibal.booking.model.entity.embeddable.Price;
import ua.mibal.booking.model.entity.embeddable.Rejection;
import ua.mibal.booking.model.entity.embeddable.ReservationDetails;
import ua.mibal.booking.model.exception.PriceForPeopleCountNotFoundException;
import ua.mibal.booking.model.exception.entity.ApartmentNotFoundException;
import ua.mibal.booking.model.exception.entity.ReservationNotFoundException;
import ua.mibal.booking.model.exception.entity.UserNotFoundException;
import ua.mibal.booking.model.mapper.ReservationMapper;
import ua.mibal.booking.model.request.ReservationRequestDto;
import ua.mibal.booking.repository.ApartmentRepository;
import ua.mibal.booking.repository.ReservationRepository;
import ua.mibal.booking.repository.UserRepository;
import ua.mibal.booking.service.util.DateTimeUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final UserRepository userRepository;
    private final ApartmentRepository apartmentRepository;
    private final ApartmentInstanceService apartmentInstanceService;
    private final DateTimeUtils dateTimeUtils;
    private final CostCalculationService costCalculationService;

    // TODO refactor

    public Page<ReservationDto> getAllByUser(String email, Pageable pageable) {
        return reservationRepository.findAllByUserEmail(email, pageable)
                .map(reservationMapper::toDto);
    }

    public Page<ReservationDto> getAll(Pageable pageable) {
        return reservationRepository.findAll(pageable)
                .map(reservationMapper::toDto);
    }

    @Transactional
    public void rejectByUser(Long id,
                             ReservationRejectingFormDto reservationRejectingFormDto,
                             String email) {
        Reservation reservation = getOneFetchRejections(id);
        if (!reservation.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("Reservation with id=" + id + " was not created " +
                                               "by User with email='" + email + "'");
        }
        rejectReservation(reservation, email, reservationRejectingFormDto.reason());
    }

    @Transactional
    public void rejectByManager(Long id,
                                ReservationRejectingFormDto reservationRejectingFormDto,
                                String email) {
        Reservation reservation = getOneFetchRejections(id);
        rejectReservation(reservation, email, reservationRejectingFormDto.reason());
    }

    @Transactional
    public void create(Long apartmentId, String userEmail, ReservationRequestDto request) {
        validateApartmentAndUserExists(apartmentId, userEmail);
        Reservation reservation = reservationOf(apartmentId, userEmail, request);
        reservationRepository.save(reservation);
    }

    private Reservation getOneFetchRejections(Long id) {
        return reservationRepository.findByIdFetchRejections(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));
    }

    private void rejectReservation(Reservation reservation, String email, String reason) {
        validateReservationToReject(reservation);
        User userReference = userRepository.getReferenceByEmail(email);
        reservation.reject(new Rejection(
                userReference,
                reason
        ));
    }

    private void validateReservationToReject(Reservation reservation) {
        // TODO validate by date or other condition
    }

    private Reservation reservationOf(Long apartmentId, String userEmail, ReservationRequestDto request) {
        User userReference = userRepository.getReferenceByEmail(userEmail);
        ApartmentInstance apartmentInstance = apartmentInstanceService
                .getFreeOne(apartmentId, request);
        ReservationDetails reservationDetails = reservationDetailsOf(apartmentId, request);
        return Reservation.builder()
                .user(userReference)
                .apartmentInstance(apartmentInstance)
                .dateTime(LocalDateTime.now())
                .details(reservationDetails)
                .state(Reservation.State.PROCESSED)
                .build();
    }

    private ReservationDetails reservationDetailsOf(Long apartmentId, ReservationRequestDto request) {
        Apartment apartment = apartmentRepository.findByIdFetchPrices(apartmentId)
                .orElseThrow(() -> new ApartmentNotFoundException(apartmentId));
        Price price = apartment.getPriceForPeople(request.people())
                .orElseThrow(() -> new PriceForPeopleCountNotFoundException(apartmentId, request.people()));
        BigDecimal fullCost = costCalculationService
                .calculatePrice(price.getCost(), request.from(), request.to());
        LocalDateTime from = dateTimeUtils.reserveFrom(request.from());
        LocalDateTime to = dateTimeUtils.reserveTo(request.to());
        return new ReservationDetails(
                from, to, fullCost, price
        );
    }

    private void validateApartmentAndUserExists(Long apartmentId, String userEmail) {
        if (!apartmentRepository.existsById(apartmentId)) {
            throw new ApartmentNotFoundException(apartmentId);
        }
        if (!userRepository.existsByEmail(userEmail)) {
            throw new UserNotFoundException(userEmail);
        }
    }
}
