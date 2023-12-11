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
import ua.mibal.booking.config.properties.BookingICalProps;
import ua.mibal.booking.model.entity.ApartmentInstance;
import ua.mibal.booking.model.entity.Event;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class BookingComReservationService {
    private final static Logger log = LoggerFactory.getLogger(BookingComReservationService.class);
    private final BookingICalProps bookingICalProps;
    private final ICalService iCalService;

    public boolean isFree(ApartmentInstance apartmentInstance, LocalDateTime start, LocalDateTime end) {
        List<Event> events = getEventsForApartmentInstance(apartmentInstance);
        Predicate<Event> intersectsWithRange =
                ev -> ev.getEnd().isAfter(start) &&
                      ev.getStart().isBefore(end);
        return events.stream().noneMatch(intersectsWithRange);
    }

    public List<Event> getEventsForApartmentInstance(ApartmentInstance apartmentInstance) {
        return apartmentInstance.getBookingIcalId()
                .map(this::iCalFileByApartmentInstance)
                .map(iCalService::eventsFromCalendarStream)
                .orElseGet(() -> {
                    log.info(
                            "No booking.com event calendar found for ApartmentInstance[id={},name={}]",
                            apartmentInstance.getId(), apartmentInstance.getName()
                    );
                    return emptyList();
                });
    }

    private InputStream iCalFileByApartmentInstance(String bookingICalId) {
        try {
            URL url = new URL(bookingICalProps.baseUrl() + bookingICalId);
            return url.openStream();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
