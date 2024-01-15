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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class BookingComReservationService {
    private final static Logger log = LoggerFactory.getLogger(BookingComReservationService.class);
    private final ICalService iCalService;

    public boolean isFree(ApartmentInstance apartmentInstance,
                          ReservationRequest reservationRequest) {
        Predicate<Event> intersectsReservationRange =
                ev -> ev.getEnd().isAfter(reservationRequest.from()) &&
                      ev.getStart().isBefore(reservationRequest.to());
        return getEvents(apartmentInstance)
                .stream()
                .noneMatch(intersectsReservationRange);
    }

    public List<Event> getEvents(ApartmentInstance apartmentInstance) {
        Optional<String> calendarUrl = apartmentInstance.getBookingICalUrl();
        if (calendarUrl.isEmpty()) {
            log.info(
                    "No booking.com event calendar found " +
                    "for ApartmentInstance[id={},name={}]",
                    apartmentInstance.getId(), apartmentInstance.getName()
            );
            return List.of();
        }
        return getEventsByCalendarUrl(calendarUrl.get());
    }

    private List<Event> getEventsByCalendarUrl(String calendarUrl) {
        try (InputStream calendar = streamFromUrl(calendarUrl)) {
            return iCalService.getEventsFromCalendarFile(calendar);
        } catch (IOException e) {
            String message = "Exception while reading ICal Calendar " +
                             "file by link: '%s'".formatted(calendarUrl);
            log.warn(message, e);
            throw new BookingComServiceException(message, e);
        }
    }

    private InputStream streamFromUrl(String calendarUrl) throws IOException {
        URL url = new URL(calendarUrl);
        return url.openStream();
    }
}
