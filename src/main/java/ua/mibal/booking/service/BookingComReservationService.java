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

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.mibal.booking.model.entity.ApartmentInstance;
import ua.mibal.booking.model.entity.Event;
import ua.mibal.booking.model.exception.service.BookingComServiceException;
import ua.mibal.booking.model.request.ReservationRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class BookingComReservationService {
    private final ICalService iCalService;

    public boolean isFreeForReservation(ApartmentInstance apartmentInstance,
                                        ReservationRequest reservationRequest) {
        List<Event> events = getEventsFor(apartmentInstance);
        Predicate<Event> eventIntersectsWithReservation =
                event -> event.getEnd().isAfter(reservationRequest.from()) &&
                         event.getStart().isBefore(reservationRequest.to());
        return events.stream()
                .noneMatch(eventIntersectsWithReservation);
    }

    public List<Event> getEventsFor(ApartmentInstance apartmentInstance) {
        Optional<String> calendarUrl = apartmentInstance.getBookingICalUrl();
        if (calendarUrl.isEmpty()) {
            return emptyList();
        }
        return getEventsByCalendarUrl(calendarUrl.get(), apartmentInstance);
    }

    private List<Event> getEventsByCalendarUrl(String calendarUrl,
                                               ApartmentInstance apartmentInstance) {
        try {
            return getEventsByCalendarUrl0(calendarUrl);
        } catch (IOException e) {
            throw new BookingComServiceException(
                    "ApartmentInstance with id='%d' has illegal ICalendar format at URL='%s'"
                            .formatted(apartmentInstance.getId(), calendarUrl), e
            );
        }
    }

    private List<Event> getEventsByCalendarUrl0(String calendarUrl) throws IOException {
        try (InputStream calendar = streamFromUrl(calendarUrl)) {
            return iCalService.getEventsFromCalendarFile(calendar);
        }
    }

    private InputStream streamFromUrl(String calendarUrl) throws IOException {
        URL url = new URL(calendarUrl);
        return url.openStream();
    }
}
