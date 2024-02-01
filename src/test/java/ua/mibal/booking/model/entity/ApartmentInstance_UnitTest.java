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

package ua.mibal.booking.model.entity;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import ua.mibal.booking.model.entity.embeddable.ReservationDetails;
import ua.mibal.booking.testUtils.DataGenerator;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ApartmentInstance_UnitTest {

    private final ApartmentInstance apartmentInstance = new ApartmentInstance();

    @Test
    void getNotRejectedEventsForNow_should_filter_Reservations_for_REJECTED_state_and_for_actual_date() {
        List<Reservation> reservations = DataGenerator.testReservations(100);
        reservations.forEach(apartmentInstance::addReservation);

        List<Reservation> expected = reservations.stream()
                .filter(Reservation::isNotRejected)
                .filter(res -> res.getDetails()
                        .getTo().isAfter(now()))
                .toList();

        List<Event> actual = apartmentInstance.getNotRejectedEventsForNow();

        assertEquals(expected, actual);
    }

    @Test
    void hasReservationsAt() {
        LocalDateTime start = now();
        LocalDateTime end = now().plusDays(1);

        List<Reservation> reservations = DataGenerator.testReservations(100);
        reservations.forEach(apartmentInstance::addReservation);

        boolean expected = reservations.stream()
                .filter(Reservation::isNotRejected)
                .anyMatch(reservation -> {
                    ReservationDetails details = reservation.getDetails();
                    return details.getFrom().isBefore(end) &&
                           details.getTo().isAfter(start);
                });

        boolean actual = apartmentInstance.hasReservationsAt(start, end);

        assertEquals(expected, actual);
    }
}
