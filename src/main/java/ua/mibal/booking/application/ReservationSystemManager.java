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

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.mibal.booking.application.dto.ReservationForm;
import ua.mibal.booking.application.port.reservation.system.ReservationSystem;
import ua.mibal.booking.domain.ApartmentInstance;
import ua.mibal.booking.domain.Event;

import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Component
public class ReservationSystemManager {
    private final List<ReservationSystem> reservationSystems;

    public List<Event> getEventsFor(ApartmentInstance apartment) {
        return reservationSystems.stream()
                .flatMap(system -> system.getEventsFor(apartment).stream())
                .toList();
    }

    public void filterForFree(List<ApartmentInstance> apartments,
                              ReservationForm form) {
        apartments.removeIf(apartment ->
                !isFreeForReservation(apartment, form));
    }

    private boolean isFreeForReservation(ApartmentInstance apartment, ReservationForm form) {
        return reservationSystems.stream()
                .allMatch(system -> system.isFreeForReservation(apartment, form));
    }
}
