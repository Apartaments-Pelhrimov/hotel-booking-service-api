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
import ua.mibal.booking.application.port.jpa.HotelTurningOffRepository;
import ua.mibal.booking.domain.ApartmentInstance;
import ua.mibal.booking.domain.Event;
import ua.mibal.booking.domain.HotelTurningOffTime;
import ua.mibal.test.annotation.UnitTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class ReservationCalendarExportService_UnitTest {

    private ReservationCalendarExportService tested;

    @Mock
    private ICalService iCalService;
    @Mock
    private HotelTurningOffRepository hotelTurningOffRepository;
    @Mock
    private ApartmentInstanceService apartmentInstanceService;
    @Mock
    private ReservationSystemManager reservationSystemManager;

    @Mock
    private ApartmentInstance apartmentInstance;
    @Mock
    private Event event1;
    @Mock
    private Event event2;
    @Mock
    private Event event3;
    @Mock
    private Event event4;
    @Mock
    private HotelTurningOffTime hotelTurningOffTime1;
    @Mock
    private HotelTurningOffTime hotelTurningOffTime2;

    @BeforeEach
    public void setup() {
        tested = new ReservationCalendarExportService(iCalService, hotelTurningOffRepository, apartmentInstanceService, reservationSystemManager);
    }

    @Test
    void getCalendarForApartmentInstanceBy() {
        Long id = 123_123L;
        String expectedICalContent = "Ical format raw string";

        when(apartmentInstanceService.getOneFetchReservations(id))
                .thenReturn(apartmentInstance);
        when(apartmentInstance.getNotRejectedEventsForNow())
                .thenReturn(List.of(event1, event2));
        when(hotelTurningOffRepository.findFromNow())
                .thenReturn(List.of(hotelTurningOffTime1, hotelTurningOffTime2));
        when(reservationSystemManager.getEventsFor(apartmentInstance))
                .thenReturn(List.of(event3, event4));
        when(iCalService.getCalendarFromEvents(argThat(arg -> arg.containsAll(
                List.of(event1, event2, hotelTurningOffTime1, hotelTurningOffTime2, event3, event4)))))
                .thenReturn(expectedICalContent);

        String actualIcal = tested.getCalendarForApartmentInstanceBy(id);

        assertEquals(expectedICalContent, actualIcal);
    }
}
