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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.mibal.booking.application.dto.response.calendar.Calendar;
import ua.mibal.booking.application.port.jpa.HotelTurningOffRepository;
import ua.mibal.booking.application.util.CollectionUtils;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.ApartmentInstance;
import ua.mibal.booking.domain.Event;
import ua.mibal.booking.domain.HotelTurningOffTime;

import java.util.Collection;
import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class CalendarService {
    private final ReservationSystemManager reservationSystemManager;
    private final ApartmentInstanceService apartmentInstanceService;
    private final ApartmentService apartmentService;
    private final TurningOffService turningOffService;
    private final ICalService iCalService;
    private final HotelTurningOffRepository hotelTurningOffRepository;

    @Transactional(readOnly = true)
    public List<Calendar> getCalendarsForApartment(Long apartmentId) {
        Apartment apartment =
                apartmentService.getOneFetchInstances(apartmentId);
        return calendarsForApartment(apartment);
    }

    @Transactional(readOnly = true)
    public Calendar getCalendarForApartmentInstance(Long instanceId) {
        ApartmentInstance instance =
                apartmentInstanceService.getOneFetchReservations(instanceId);
        List<? extends Event> hotelEvents =
                turningOffService.getForHotelForNow();
        return calendarForApartmentInstance(instance, hotelEvents);
    }

    @Transactional(readOnly = true) // For LAZY ApartmentInstance.turningOffTimes loading
    public String getICalForApartmentInstance(Long instanceId) {
        ApartmentInstance apartmentInstance =
                apartmentInstanceService.getOneFetchReservations(instanceId);
        Collection<Event> allEvents =
                getAllActualLocalEventsFor(apartmentInstance);
        return iCalService.getCalendarFromEvents(allEvents);
    }

    private Collection<Event> getAllActualLocalEventsFor(ApartmentInstance apartmentInstance) {
        List<HotelTurningOffTime> hotelEvents =
                hotelTurningOffRepository.findFromNow();
        List<Event> apartmentEvents =
                apartmentInstance.getNotRejectedEventsForNow();
        return CollectionUtils.union(apartmentEvents, hotelEvents);
    }

    private List<Calendar> calendarsForApartment(Apartment apartment) {
        List<? extends Event> hotelEvents =
                turningOffService.getForHotelForNow();
        return apartment.getApartmentInstances()
                .stream()
                .map(instance ->
                        calendarForApartmentInstance(instance, hotelEvents))
                .toList();
    }

    private Calendar calendarForApartmentInstance(ApartmentInstance apartmentInstance,
                                                  List<? extends Event> hotelEvents) {
        List<Event> localApartmentInstanceEvents =
                apartmentInstance.getNotRejectedEventsForNow();
        List<Event> integratedSystemEvents =
                reservationSystemManager.getEventsFor(apartmentInstance);
        Collection<Event> events =
                CollectionUtils.union(localApartmentInstanceEvents, hotelEvents, integratedSystemEvents);
        return Calendar.of(events);
    }
}
