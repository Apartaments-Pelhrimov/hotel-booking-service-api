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
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.ApartmentInstance;
import ua.mibal.booking.domain.Event;
import ua.mibal.booking.domain.id.ApartmentId;

import java.util.List;

import static ua.mibal.booking.application.util.CollectionUtils.union;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class EventService {
    private final ReservationSystemManager reservationSystemManager;
    private final ApartmentInstanceService apartmentInstanceService;
    private final ApartmentService apartmentService;
    private final TurningOffService turningOffService;

    @Transactional(readOnly = true)
    public List<List<Event>> getEventsForApartmentBy(ApartmentId apartmentId) {
        Apartment apartment = apartmentService.getOneFetchInstances(apartmentId);
        List<? extends Event> hotelEvents = turningOffService.getForHotelForNow();
        return apartment.getApartmentInstances().stream()
                .map(instance -> eventsForApartmentInstance(instance, hotelEvents))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Event> getEventsForApartmentInstanceBy(Long instanceId) {
        ApartmentInstance instance = apartmentInstanceService.getOneFetchReservations(instanceId);
        List<? extends Event> hotelEvents = turningOffService.getForHotelForNow();
        return eventsForApartmentInstance(instance, hotelEvents);
    }

    private List<Event> eventsForApartmentInstance(ApartmentInstance apartmentInstance,
                                                   List<? extends Event> hotelEvents) {
        List<Event> localApartmentInstanceEvents = apartmentInstance.getNotRejectedEventsForNow();
        List<Event> integratedSystemEvents = reservationSystemManager.getEventsFor(apartmentInstance);
        return union(localApartmentInstanceEvents, hotelEvents, integratedSystemEvents);
    }
}
