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
import ua.mibal.booking.model.entity.Reservation;
import ua.mibal.booking.model.entity.User;
import ua.mibal.booking.model.entity.embeddable.Rejection;
import ua.mibal.booking.model.exception.entity.ReservationNotFoundException;
import ua.mibal.booking.model.mapper.ReservationMapper;
import ua.mibal.booking.repository.ReservationRepository;
import ua.mibal.booking.repository.UserRepository;

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
}
