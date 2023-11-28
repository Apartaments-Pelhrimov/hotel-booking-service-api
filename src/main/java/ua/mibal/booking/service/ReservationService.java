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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.mibal.booking.model.dto.request.ReservationRejectingFormDto;
import ua.mibal.booking.model.dto.response.ReservationDto;
import ua.mibal.booking.model.entity.Reservation;
import ua.mibal.booking.model.entity.User;
import ua.mibal.booking.model.entity.embeddable.Role;
import ua.mibal.booking.model.mapper.ReservationMapper;
import ua.mibal.booking.repository.ReservationRepository;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@RequiredArgsConstructor
@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final UserService userService;

    public Page<ReservationDto> getReservationsByAuthentication(Authentication authentication, Pageable pageable) {
        String email = authentication.getName();
        return reservationRepository.findAllByUserEmail(email, pageable)
                .map(reservationMapper::toDto);
    }

    public Page<ReservationDto> getAllReservations(Pageable pageable) {
        return reservationRepository.findAll(pageable)
                .map(reservationMapper::toDto);
    }

    @Transactional
    public void reject(Long id,
                       ReservationRejectingFormDto reservationRejectingFormDto,
                       Authentication authentication) {
        User user = userService.getOneByEmail(authentication.getName());
        Reservation reservation = getOneById(id);
        if (user.is(Role.ROLE_USER) &&
            !reservation.getUser().equals(user)) {
            throw new IllegalArgumentException("Reservation with id=" + id + "was not created " +
                                               "by User with email=" + authentication.getName());
        }
        rejectReservation(reservation, user, reservationRejectingFormDto.reason());
    }

    public Reservation getOneById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity Reservation by id=" + id + " not found"));
    }

    private void rejectReservation(Reservation reservation, User user, String reason) {
        // TODO add ReservationRejection entity
        validateReservationToReject(reservation);
        reservation.reject();
//        reservationRejectionRepository.save(new ReservationRejection(
//                user,
//                reservation,
//                reason
//        ));
    }

    private void validateReservationToReject(Reservation reservation) {
        // TODO validate by date or other condition
    }
}
