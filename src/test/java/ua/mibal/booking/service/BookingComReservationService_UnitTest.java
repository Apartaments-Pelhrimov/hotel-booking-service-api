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

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ua.mibal.booking.config.properties.BookingICalProps;
import ua.mibal.booking.model.entity.ApartmentInstance;
import ua.mibal.booking.model.entity.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BookingComReservationService.class)
@TestPropertySource(locations = "classpath:application.yaml")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestMethodOrder(OrderAnnotation.class)
class BookingComReservationService_UnitTest {

    @Autowired
    private BookingComReservationService service;

    @MockBean
    private BookingICalProps bookingICalProps;
    @MockBean
    private ICalService iCalService;

    @Mock
    private ApartmentInstance apartmentInstance;
    @Mock
    private Event event;

    @Test
    @Order(1)
    void getEventsForApartmentInstance() {
        String calendarUrl = "file:/Users/admin/IdeaProjects/hotel-booking-service/src/test/resources";
        when(apartmentInstance.getBookingIcalId())
                .thenReturn(Optional.of("/test.ics"));
        when(bookingICalProps.baseUrl()).thenReturn(calendarUrl);
        when(iCalService.eventsFromCalendarStream(any()))
                .thenReturn(of(event));

        List<Event> actual = service.getEventsForApartmentInstance(apartmentInstance);

        assertEquals(of(event), actual);
    }

    @Test
    @Order(2)
    void getEventsForApartmentInstance_should_not_throw_NotFoundException() {
        when(apartmentInstance.getBookingIcalId()).thenReturn(Optional.empty());

        assertDoesNotThrow(
                () -> service.getEventsForApartmentInstance(apartmentInstance)
        );
    }

    @ParameterizedTest
    @Order(3)
    @CsvSource({
            "invalid_url\"",
            "valid_url",
            "./valid_url/",
            "//valid_url/",
            "~/valid_url/",
    })
    void getEventsForApartmentInstance_should_throw_IllegalArgumentException_if_uri_is_invalid(String uri) {
        when(apartmentInstance.getBookingIcalId()).thenReturn(Optional.of("id"));
        when(bookingICalProps.baseUrl()).thenReturn(uri);

        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> service.getEventsForApartmentInstance(apartmentInstance)
        );
    }

    @ParameterizedTest
    @Order(4)
    @MethodSource("ua.mibal.booking.testUtils.DataGenerator#eventsFactory")
    void isFree(List<Event> events, LocalDateTime from, LocalDateTime to, boolean expected) {
        when(apartmentInstance.getBookingIcalId()).thenReturn(Optional.of("/test.ics"));
        when(bookingICalProps.baseUrl())
                .thenReturn("file:/Users/admin/IdeaProjects/hotel-booking-service/src/test/resources");
        when(iCalService.eventsFromCalendarStream(any()))
                .thenReturn(events);

        boolean actual = service.isFree(apartmentInstance, from, to);

        assertEquals(expected, actual);
    }
}
