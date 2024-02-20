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

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.mibal.booking.application.port.jpa.HotelTurningOffRepository;
import ua.mibal.booking.domain.ApartmentInstance;
import ua.mibal.booking.domain.Event;

import java.util.List;

import static ua.mibal.booking.application.util.CollectionUtils.union;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Component
public class ReservationCalendarExportService {
    private final ICalService iCalService;
    private final HotelTurningOffRepository hotelTurningOffRepository;
    private final ApartmentInstanceService apartmentInstanceService;
    private final ReservationSystemManager reservationSystemManager;

    @Transactional(readOnly = true) // For LAZY ApartmentInstance.turningOffTimes loading
    public String getCalendarForApartmentInstanceBy(Long apartmentInstanceId) {
        ApartmentInstance apartmentInstance =
                apartmentInstanceService.getOneFetchReservations(apartmentInstanceId);
        List<Event> events = getActualEventsFor(apartmentInstance);
        return iCalService.getCalendarFromEvents(events);
    }

    private List<Event> getActualEventsFor(ApartmentInstance apartmentInstance) {
        var apartmentEvents = apartmentInstance.getNotRejectedEventsForNow();
        var localHotelEvents = hotelTurningOffRepository.findFromNow();
        var anotherReservationSystemEvents = reservationSystemManager.getEventsFor(apartmentInstance);
        return union(localHotelEvents, apartmentEvents, anotherReservationSystemEvents);
    }
}
