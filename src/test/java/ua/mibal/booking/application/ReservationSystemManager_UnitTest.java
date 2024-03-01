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
import ua.mibal.booking.application.port.reservation.system.ReservationSystem;
import ua.mibal.booking.domain.ApartmentInstance;
import ua.mibal.booking.domain.Event;
import ua.mibal.booking.application.dto.ReservationForm;
import ua.mibal.test.annotation.UnitTest;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class ReservationSystemManager_UnitTest {

    private ReservationSystemManager tested;

    @Mock
    private ReservationSystem reservationSystem1;
    @Mock
    private ReservationSystem reservationSystem2;

    @Mock
    private ApartmentInstance apartmentInstance;
    @Mock
    private ApartmentInstance apartmentInstance1;
    @Mock
    private ApartmentInstance apartmentInstance2;
    @Mock
    private Event event1;
    @Mock
    private Event event2;
    @Mock
    private Event event3;
    @Mock
    private ReservationForm form;

    @BeforeEach
    public void setup() {
        tested = new ReservationSystemManager(List.of(reservationSystem1, reservationSystem2));
    }

    @Test
    void getEventsFor() {
        when(reservationSystem1.getEventsFor(apartmentInstance))
                .thenReturn(List.of(event1, event2));
        when(reservationSystem2.getEventsFor(apartmentInstance))
                .thenReturn(List.of(event3));

        List<Event> actual = tested.getEventsFor(apartmentInstance);

        assertThat(actual).contains(event1, event2, event3);
    }

    @Test
    void getEventsFor_returns_empty_if_there_are_not_reservation_systems() {
        tested = new ReservationSystemManager(emptyList());

        List<Event> actual = tested.getEventsFor(null);

        assertThat(actual).isEmpty();
    }

    @Test
    void filterForFree_should_remove_if_at_least_one_ReservationSystem_return_isFree_false() {
        List<ApartmentInstance> apartmentInstances = new ArrayList<>(List.of(apartmentInstance1));

        when(reservationSystem1.isFreeForReservation(apartmentInstance1, form))
                .thenReturn(true);
        when(reservationSystem2.isFreeForReservation(apartmentInstance1, form))
                .thenReturn(false);

        tested.filterForFree(apartmentInstances, form);

        assertThat(apartmentInstances).isEmpty();
    }

    @Test
    void filterForFree_should_not_remove_if_ApartmentInstance_isFree() {
        List<ApartmentInstance> apartmentInstances = new ArrayList<>(List.of(apartmentInstance1));

        when(reservationSystem1.isFreeForReservation(apartmentInstance1, form))
                .thenReturn(true);
        when(reservationSystem2.isFreeForReservation(apartmentInstance1, form))
                .thenReturn(true);

        tested.filterForFree(apartmentInstances, form);

        assertThat(apartmentInstances).containsOnly(apartmentInstance1);
    }

    @Test
    void filterForFree_should_not_remove_if_there_are_not_reservation_systems() {
        tested = new ReservationSystemManager(emptyList());

        List<ApartmentInstance> apartmentInstances =
                new ArrayList<>(List.of(apartmentInstance1, apartmentInstance2));

        tested.filterForFree(apartmentInstances, form);

        assertThat(apartmentInstances).containsOnly(apartmentInstance1, apartmentInstance2);
    }
}
