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
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.ApartmentInstance;
import ua.mibal.booking.domain.Event;
import ua.mibal.booking.domain.HotelTurningOffTime;
import ua.mibal.test.annotation.UnitTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class EventService_UnitTest {

    private EventService service;
    @Mock
    private ReservationSystemManager reservationSystemManager;
    @Mock
    private ApartmentInstanceService apartmentInstanceService;
    @Mock
    private ApartmentService apartmentService;
    @Mock
    private TurningOffService turningOffService;

    @Mock
    private Apartment apartment;
    @Mock
    private ApartmentInstance apartmentInstance;

    @Mock
    private Event apartmentInstanceEvent;
    @Mock
    private Event bookingComEvent;
    @Mock
    private HotelTurningOffTime hotelTurningOffTime;

    @BeforeEach
    void setup() {
        service = new EventService(reservationSystemManager, apartmentInstanceService, apartmentService, turningOffService);
    }

    @Test
    void getEventsForApartmentBy() {
        Long apartmentId = 1L;

        when(apartmentService.getOneFetchInstances(apartmentId))
                .thenReturn(apartment);
        when(turningOffService.getForHotelForNow())
                .thenReturn(List.of(hotelTurningOffTime));
        when(apartment.getApartmentInstances())
                .thenReturn(List.of(apartmentInstance));

        when(apartmentInstance.getNotRejectedEventsForNow())
                .thenReturn(List.of(apartmentInstanceEvent));
        when(reservationSystemManager.getEventsFor(apartmentInstance))
                .thenReturn(List.of(bookingComEvent));

        List<List<Event>> actual = service.getEventsForApartmentBy(apartmentId);

        assertThat(actual.get(0)).containsOnly(apartmentInstanceEvent, hotelTurningOffTime, bookingComEvent);
    }

    @Test
    void getEventsForApartmentInstanceBy() {
        Long instanceId = 1L;

        when(apartmentInstanceService.getOneFetchReservations(instanceId))
                .thenReturn(apartmentInstance);
        when(turningOffService.getForHotelForNow())
                .thenReturn(List.of(hotelTurningOffTime));

        when(apartmentInstance.getNotRejectedEventsForNow())
                .thenReturn(List.of(apartmentInstanceEvent));
        when(reservationSystemManager.getEventsFor(apartmentInstance))
                .thenReturn(List.of(bookingComEvent));

        List<Event> actual = service.getEventsForApartmentInstanceBy(instanceId);

        assertThat(actual).containsOnly(apartmentInstanceEvent, hotelTurningOffTime, bookingComEvent);
    }
}
