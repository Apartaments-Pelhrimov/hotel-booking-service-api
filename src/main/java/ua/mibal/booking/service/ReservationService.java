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
import ua.mibal.booking.model.exception.entity.ApartmentNotFoundException;
import ua.mibal.booking.model.exception.entity.PriceForPeopleCountNotFoundException;
import ua.mibal.booking.model.exception.entity.ReservationNotFoundException;
import ua.mibal.booking.model.exception.entity.UserNotFoundException;
import ua.mibal.booking.model.mapper.ReservationMapper;
import ua.mibal.booking.repository.ApartmentRepository;
import ua.mibal.booking.repository.ReservationRepository;
import ua.mibal.booking.repository.UserRepository;
import ua.mibal.booking.service.util.DateUtils;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Date;

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
    private final ApartmentService apartmentService;
    private final DateUtils dateUtils;
    private final CostCalculationService costCalculationService;

    public Page<ReservationDto> getReservationsByUser(String email, Pageable pageable) {
        return reservationRepository.findAllByUserEmail(email, pageable)
                .map(reservationMapper::toDto);
    }

    public Page<ReservationDto> getAllReservations(Pageable pageable) {
        return reservationRepository.findAll(pageable)
                .map(reservationMapper::toDto);
    }

    @Transactional
    public void rejectByUser(Long id,
                             ReservationRejectingFormDto reservationRejectingFormDto,
                             String email) {
        Reservation reservation = getOneByIdFetchRejections(id);
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
        Reservation reservation = getOneByIdFetchRejections(id);
        rejectReservation(reservation, email, reservationRejectingFormDto.reason());
    }

    public Reservation getOneById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));
    }

    private Reservation getOneByIdFetchRejections(Long id) {
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

    @Transactional
    public void reserveApartment(Long apartmentId, String userEmail, Date from, Date to, Integer people) {
        validateApartmentAndUserExists(apartmentId, userEmail);
        Reservation reservation = reservationOf(apartmentId, userEmail, from, to, people);
        reservationRepository.save(reservation);
    }

    private Reservation reservationOf(Long apartmentId, String userEmail, Date from, Date to, Integer people) {
        User userReference = userRepository.getReferenceByEmail(userEmail);
        ApartmentInstance apartmentInstance = apartmentService
                .getFreeApartmentInstanceByApartmentId(apartmentId, from, to);
        ReservationDetails reservationDetails = reservationDetailsOf(apartmentId, from, to, people);
        return Reservation.builder()
                .user(userReference)
                .apartmentInstance(apartmentInstance)
                .dateTime(dateUtils.now())
                .details(reservationDetails)
                .build();
    }

    private ReservationDetails reservationDetailsOf(Long apartmentId, Date fromDate, Date toDate, Integer people) {
        Apartment apartment = apartmentRepository.findByIdFetchPrices(apartmentId)
                .orElseThrow(() -> new ApartmentNotFoundException(apartmentId));
        Price price = apartment.getPriceForPeople(people)
                .orElseThrow(() -> new PriceForPeopleCountNotFoundException(apartmentId, people));
        BigDecimal fullCost = costCalculationService
                .calculateFullPriceForDays(price.getCost(), fromDate, toDate);
        ZonedDateTime from = dateUtils.reservationFrom(fromDate);
        ZonedDateTime to = dateUtils.reservationTo(fromDate);
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
