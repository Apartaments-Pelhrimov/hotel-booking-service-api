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

package ua.mibal.booking.service;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ua.mibal.booking.model.dto.response.calendar.Calendar;
import ua.mibal.booking.model.entity.Apartment;
import ua.mibal.booking.model.entity.ApartmentInstance;
import ua.mibal.booking.model.entity.Event;
import ua.mibal.booking.model.entity.HotelTurningOffTime;
import ua.mibal.booking.repository.HotelTurningOffRepository;

import java.util.List;

import static java.time.LocalDateTime.now;
import static org.apache.commons.collections4.CollectionUtils.union;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CalendarService.class)
@TestPropertySource(locations = "classpath:application.yaml")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CalendarService_UnitTest {

    @Autowired
    private CalendarService service;

    @MockBean
    private BookingComReservationService bookingComReservationService;
    @MockBean
    private ApartmentInstanceService apartmentInstanceService;
    @MockBean
    private ApartmentService apartmentService;
    @MockBean
    private TurningOffService turningOffService;
    @MockBean
    private ICalService iCalService;
    @MockBean
    private HotelTurningOffRepository hotelTurningOffRepository;

    @Mock
    private Apartment apartment;
    @Mock
    private ApartmentInstance apartmentInstance;

    private Event apartmentInstanceEvent = spy(Event.from(
            now(), now().plusDays(1), "ApartmentInstanceEvent"
    ));
    private Event bookingComEvent = spy(Event.from(
            now(), now().plusDays(1), "BookingComEvent"
    ));
    private HotelTurningOffTime hotelTurningOffTime = spy(new HotelTurningOffTime(
            1L, now(), now().plusDays(2), "Christmas holidays"
    ));

    @Test
    void getCalendarsForApartment() {
        Long apartmentId = 1L;

        when(apartmentService.getOneFetchInstances(apartmentId))
                .thenReturn(apartment);
        when(turningOffService.getForHotelForNow())
                .thenReturn(List.of(hotelTurningOffTime));
        when(apartment.getApartmentInstances())
                .thenReturn(List.of(apartmentInstance));

        when(apartmentInstance.getNotRejectedEventsForNow())
                .thenReturn(List.of(apartmentInstanceEvent));
        when(bookingComReservationService.getEventsFor(apartmentInstance))
                .thenReturn(List.of(bookingComEvent));

        List<Event> expectedEvents =
                List.of(apartmentInstanceEvent, hotelTurningOffTime, bookingComEvent);
        List<Calendar> expected = List.of(Calendar.of(expectedEvents));

        List<Calendar> actual = service.getCalendarsForApartment(apartmentId);

        assertEquals(expected, actual);
    }

    @Test
    void getCalendarForApartmentInstance() {
        Long instanceId = 1L;

        when(apartmentInstanceService.getOneFetchReservations(instanceId))
                .thenReturn(apartmentInstance);
        when(turningOffService.getForHotelForNow())
                .thenReturn(List.of(hotelTurningOffTime));

        when(apartmentInstance.getNotRejectedEventsForNow())
                .thenReturn(List.of(apartmentInstanceEvent));
        when(bookingComReservationService.getEventsFor(apartmentInstance))
                .thenReturn(List.of(bookingComEvent));

        List<Event> expectedEvents =
                List.of(apartmentInstanceEvent, hotelTurningOffTime, bookingComEvent);
        Calendar expected = Calendar.of(expectedEvents);

        Calendar actual = service.getCalendarForApartmentInstance(instanceId);

        assertEquals(expected, actual);
    }

    @Test
    void getICalForApartmentInstance() {
        Long instanceId = 1L;

        when(apartmentInstanceService.getOneFetchReservations(instanceId))
                .thenReturn(apartmentInstance);
        when(hotelTurningOffRepository.findFromNow())
                .thenReturn(List.of(hotelTurningOffTime, hotelTurningOffTime));
        when(apartmentInstance.getNotRejectedEventsForNow())
                .thenReturn(List.of(apartmentInstanceEvent, apartmentInstanceEvent));


        service.getICalForApartmentInstance(instanceId);

        verify(iCalService, times(1)).getCalendarFromEvents(
                union(List.of(hotelTurningOffTime, hotelTurningOffTime),
                        List.of(apartmentInstanceEvent, apartmentInstanceEvent))
        );
    }
}
