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

package ua.mibal.booking.application.port.reservation.system;

import ua.mibal.booking.application.model.ReservationForm;
import ua.mibal.booking.domain.ApartmentInstance;
import ua.mibal.booking.domain.Event;

import java.util.List;

/**
 * The {@code ReservationSystem} interface defines the contract for an integration with the
 * appropriate reservation system.
 * Responsible for retrieving events from another system and checking whether the instance is
 * available for a given reservation.
 *
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public interface ReservationSystem {

    /**
     * Retrieves a list of {@code Event} objects associated with the specified
     * {@code ApartmentInstance}.
     *
     * @param apartmentInstance The {@code ApartmentInstance} for which events are to be retrieved.
     * @return A {@code List} of {@code Event} objects representing events associated with the
     * apartment instance.
     */
    List<Event> getEventsFor(ApartmentInstance apartmentInstance);

    /**
     * Checks whether the specified {@code ApartmentInstance} is available for reservation based on
     * the provided {@code ReservationForm} with reservation range.
     *
     * @param apartmentInstance The {@code ApartmentInstance} to be checked for availability.
     * @param reservationForm   The {@code ReservationForm} containing details for the reservation.
     * @return {@code true} if the apartment instance is available for reservation;
     * {@code false} otherwise.
     */
    boolean isFreeForReservation(ApartmentInstance apartmentInstance,
                                 ReservationForm reservationForm);
}
