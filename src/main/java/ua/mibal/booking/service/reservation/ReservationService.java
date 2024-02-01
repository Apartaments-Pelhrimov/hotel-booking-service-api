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

package ua.mibal.booking.service.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.mibal.booking.model.dto.response.ReservationDto;
import ua.mibal.booking.model.entity.Reservation;
import ua.mibal.booking.model.entity.User;
import ua.mibal.booking.model.exception.UserHasNoAccessToReservationException;
import ua.mibal.booking.model.exception.entity.ReservationNotFoundException;
import ua.mibal.booking.model.mapper.ReservationMapper;
import ua.mibal.booking.model.request.ReservationRequest;
import ua.mibal.booking.repository.ReservationRepository;
import ua.mibal.booking.service.UserService;
import ua.mibal.booking.service.reservation.component.ReservationBuilder;

import static ua.mibal.booking.model.entity.embeddable.Role.MANAGER;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final UserService userService;
    private final ReservationBuilder reservationBuilder;

    public Page<ReservationDto> getAllByUser(String email, Pageable pageable) {
        return reservationRepository.findAllByUserEmail(email, pageable)
                .map(reservationMapper::toDto);
    }

    public Page<ReservationDto> getAll(Pageable pageable) {
        return reservationRepository.findAll(pageable)
                .map(reservationMapper::toDto);
    }

    @Transactional
    public void rejectReservation(Long id,
                                  String email,
                                  String reason) {
        Reservation reservation = getOneFetchRejections(id);
        User user = userService.getOne(email);
        validateUserHasAccessToReservation(user, reservation);
        validateReservationToReject(reservation);
        reservation.reject(user, reason);
    }

    @Transactional
    public void reserve(ReservationRequest request) {
        Reservation reservation = reservationBuilder.buildBy(request);
        reservationRepository.save(reservation);
    }

    private Reservation getOneFetchRejections(Long id) {
        return reservationRepository.findByIdFetchRejections(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));
    }

    private void validateReservationToReject(Reservation reservation) {
        // TODO validate by date or other condition
    }

    private void validateUserHasAccessToReservation(User user, Reservation reservation) {
        if (user.is(MANAGER)) {
            return;
        }
        User reservationOwner = reservation.getUser();
        if (!reservationOwner.equals(user)) {
            throw new UserHasNoAccessToReservationException();
        }
    }
}
