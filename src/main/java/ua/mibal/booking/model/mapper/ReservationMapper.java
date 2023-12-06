/*
 * Copyright (c) 2023. Mykhailo Balakhon, mailto:9mohapx9@gmail.com
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

package ua.mibal.booking.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ua.mibal.booking.model.dto.response.Calendar;
import ua.mibal.booking.model.dto.response.Calendar.Range;
import ua.mibal.booking.model.dto.response.ReservationDto;
import ua.mibal.booking.model.entity.ApartmentInstance;
import ua.mibal.booking.model.entity.Event;
import ua.mibal.booking.model.entity.HotelTurningOffTime;
import ua.mibal.booking.model.entity.Reservation;

import java.util.Collection;
import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.union;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Mapper(uses = PhotoMapper.class,
        componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class ReservationMapper {

    @Mapping(target = "date", source = "dateTime")
    public abstract ReservationDto toDto(Reservation reservation);

    public List<Calendar> toCalendarList(List<ApartmentInstance> apartmentInstances,
                                         List<HotelTurningOffTime> hotelTurningOffTimes) {
        return apartmentInstances.stream()
                .map(ai -> toCalendar(ai, hotelTurningOffTimes))
                .toList();
    }

    public Calendar toCalendar(ApartmentInstance apartmentInstance,
                               List<HotelTurningOffTime> hotelTurningOffTimes) {
        Collection<Event> events = eventsFromEntities(apartmentInstance, hotelTurningOffTimes);
        List<Range> ranges = rangesFromEvents(events);
        return new Calendar(apartmentInstance.getId(), ranges);
    }

    private Collection<Event> eventsFromEntities(ApartmentInstance apartmentInstance, List<HotelTurningOffTime> hotelTurningOffTimes) {
        Collection<Event> apartmentEvents = union(
                apartmentInstance.getReservations(),
                apartmentInstance.getTurningOffTimes()
        );
        return union(apartmentEvents, hotelTurningOffTimes);
    }

    private List<Range> rangesFromEvents(Collection<Event> events) {
        return events.stream()
                .map(this::eventToRange)
                .toList();
    }

    private Range eventToRange(Event event) {
        int start = event.getStart()
                .getDayOfMonth();
        int end = event.getStart()
                .getDayOfMonth();
        return Range.of(start, end - 1);
    }
}
