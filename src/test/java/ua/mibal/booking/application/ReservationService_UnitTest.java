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

package ua.mibal.booking.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import ua.mibal.booking.application.component.ReservationBuilder;
import ua.mibal.booking.application.exception.UserHasNoAccessToReservationException;
import ua.mibal.booking.application.model.ReservationForm;
import ua.mibal.booking.application.model.ReservationRejectingForm;
import ua.mibal.booking.application.port.jpa.ReservationRepository;
import ua.mibal.booking.domain.Reservation;
import ua.mibal.booking.domain.User;
import ua.mibal.test.annotation.UnitTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Pageable.unpaged;
import static ua.mibal.booking.domain.Role.MANAGER;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class ReservationService_UnitTest {

    private ReservationService service;

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private UserService userService;
    @Mock
    private ReservationBuilder reservationBuilder;

    @Mock
    private Reservation reservation;
    @Mock
    private User user;
    @Mock
    private User anotherUser;
    @Mock
    private ReservationForm form;

    @BeforeEach
    void setup() {
        service = new ReservationService(reservationRepository, userService, reservationBuilder);
    }

    @Test
    void getAllByUser() {
        String email = "userEmail123";

        when(reservationRepository.findAllByUserEmail(email, unpaged()))
                .thenReturn(new PageImpl<>(List.of(reservation, reservation, reservation)));

        var actual = service.getAllByUser(email, unpaged());

        assertEquals(
                new PageImpl<>(List.of(reservation, reservation, reservation)),
                actual
        );
    }

    @Test
    void getAll() {
        when(reservationRepository.findAll(unpaged()))
                .thenReturn(new PageImpl<>(List.of(reservation, reservation, reservation)));

        var actual = service.getAll(unpaged());

        assertEquals(
                new PageImpl<>(List.of(reservation, reservation, reservation)),
                actual
        );
    }

    @Test
    void rejectReservation_by_MANAGER() {
        Long id = 1L;
        String email = "userEmail123";
        String reason = "reason";


        when(reservationRepository.findByIdFetchRejections(id))
                .thenReturn(Optional.of(reservation));
        when(userService.getOne(email))
                .thenReturn(user);
        when(user.is(MANAGER))
                .thenReturn(true);

        service.rejectReservation(new ReservationRejectingForm(id, email, reason));

        verify(reservation, times(1))
                .reject(user, reason);
    }

    @Test
    void rejectReservation_by_USER() {
        Long id = 1L;
        String email = "userEmail123";
        String reason = "reason";


        when(reservationRepository.findByIdFetchRejections(id))
                .thenReturn(Optional.of(reservation));
        when(userService.getOne(email))
                .thenReturn(user);
        when(user.is(MANAGER))
                .thenReturn(false);
        when(reservation.getUser())
                .thenReturn(user);

        service.rejectReservation(new ReservationRejectingForm(id, email, reason));

        verify(reservation, times(1))
                .reject(user, reason);
    }

    @Test
    void rejectReservation_should_throw_UserHasNoAccessToReservationException() {
        Long id = 1L;
        String email = "userEmail123";
        String reason = "reason";

        when(reservationRepository.findByIdFetchRejections(id))
                .thenReturn(Optional.of(reservation));
        when(userService.getOne(email))
                .thenReturn(user);
        when(user.is(MANAGER))
                .thenReturn(false);
        when(reservation.getUser())
                .thenReturn(anotherUser);

        assertThrows(UserHasNoAccessToReservationException.class,
                () -> service.rejectReservation(new ReservationRejectingForm(id, email, reason)));
    }

    @Test
    void reserve() {
        when(reservationBuilder.buildBy(form))
                .thenReturn(reservation);

        service.reserve(form);

        verify(reservationRepository, times(1))
                .save(reservation);
    }
}
