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

package ua.mibal.booking.application.mapper;

import lombok.RequiredArgsConstructor;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DateProperty;
import org.springframework.stereotype.Component;
import ua.mibal.booking.config.properties.CalendarProps;
import ua.mibal.booking.domain.DefaultEvent;
import ua.mibal.booking.domain.Event;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

import static java.util.Date.from;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Component
public class CalendarFormatMapper {
    private final CalendarProps calendarProps;

    public List<VEvent> eventsToVEvents(Collection<Event> events) {
        return events.stream()
                .map(this::eventToVEvent)
                .toList();
    }

    public List<Event> vEventsToEvents(List<VEvent> vEvents) {
        return vEvents.stream()
                .map(this::vEventToEvent)
                .toList();
    }


    private VEvent eventToVEvent(Event event) {
        return new VEvent(
                toIcal(event.getStart(), calendarProps.zoneId()),
                toIcal(event.getEnd(), calendarProps.zoneId()),
                event.getEventName()
        );
    }

    private Event vEventToEvent(VEvent vEvent) {
        return new DefaultEvent(
                fromICal(vEvent.getStartDate(), calendarProps.zoneId()),
                fromICal(vEvent.getEndDate(), calendarProps.zoneId()),
                vEvent.getSummary().getValue()
        );
    }

    private LocalDateTime fromICal(DateProperty dateProperty, ZoneId targetZoneId) {
        Instant instant = dateProperty.getDate().toInstant();
        ZonedDateTime zonedDateTimeAtOurZone = instant.atZone(targetZoneId);
        return zonedDateTimeAtOurZone.toLocalDateTime();
    }

    private DateTime toIcal(LocalDateTime localDateTime, ZoneId sourceZoneId) {
        Instant instant = localDateTime.atZone(sourceZoneId).toInstant();
        TimeZone timeZone = iCaltimeZone(sourceZoneId.getId());
        return new DateTime(from(instant), timeZone);
    }

    private TimeZone iCaltimeZone(String id) {
        TimeZoneRegistry registry = new CalendarBuilder().getRegistry();
        return registry.getTimeZone(id);
    }
}
