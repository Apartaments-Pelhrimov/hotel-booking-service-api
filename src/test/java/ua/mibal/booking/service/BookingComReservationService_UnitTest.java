/*
 * Copyright (c) 2023. Mykhailo Balakhon mailto:9mohapx9@gmail.com
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ua.mibal.booking.model.entity.ApartmentInstance;
import ua.mibal.booking.model.entity.Event;
import ua.mibal.booking.model.exception.service.BookingComServiceException;
import ua.mibal.booking.model.request.ReservationRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.List.of;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@TestMethodOrder(OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BookingComReservationService_UnitTest {
    private final static String calendarUrl =
            "file:/Users/admin/IdeaProjects/hotel-booking-service/" +
            "src/test/resources/test.ics";

    private BookingComReservationService service;

    @Mock
    private ICalService iCalService;

    @Mock
    private ApartmentInstance apartmentInstance;
    @Mock
    private Event event;

    @BeforeEach
    void setup() {
        service = new BookingComReservationService(iCalService);
    }

    @Test
    @Order(1)
    void getEventsFor() {
        when(apartmentInstance.getBookingICalUrl()).thenReturn(Optional.of(calendarUrl));
        when(iCalService.getEventsFromCalendarFile(any())).thenReturn(of(event));

        List<Event> actual = service.getEventsFor(apartmentInstance);

        assertEquals(of(event), actual);
    }

    @Test
    @Order(2)
    void getEventsFor_should_NOT_throw_if_apartmentInstance_has_not_ical_url() {
        when(apartmentInstance.getBookingICalUrl()).thenReturn(Optional.empty());

        assertDoesNotThrow(
                () -> service.getEventsFor(apartmentInstance)
        );
    }

    @ParameterizedTest
    @Order(3)
    @CsvSource({
            "invalid_url\"",
            "invalid_url",
            "./invalid_url/",
            "//invalid_url/",
            "~/invalid_url/",
    })
    void getEventsFor_should_throw_BookingComServiceException_if_ical_url_is_invalid(String url) {
        when(apartmentInstance.getBookingICalUrl()).thenReturn(Optional.of(url));

        assertThrows(
                BookingComServiceException.class,
                () -> service.getEventsFor(apartmentInstance)
        );
    }

    @ParameterizedTest
    @Order(4)
    @MethodSource("ua.mibal.booking.testUtils.DataGenerator#eventsFactory")
    void isFreeForReservation(List<Event> events, LocalDateTime from, LocalDateTime to, boolean expected) {
        when(apartmentInstance.getBookingICalUrl()).thenReturn(Optional.of(calendarUrl));
        when(iCalService.getEventsFromCalendarFile(any()))
                .thenReturn(events);

        boolean actual = service.isFreeForReservation(apartmentInstance, new ReservationRequest(from, to, -1, -1L));

        assertEquals(expected, actual);
    }
}
