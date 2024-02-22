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

package ua.mibal.booking.adapter.in.web.mapper;

import lombok.RequiredArgsConstructor;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.component.VEvent;
import org.springframework.stereotype.Component;
import ua.mibal.booking.config.properties.CalendarProps;
import ua.mibal.booking.domain.Event;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Date.from;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Component
public class ICalEventMapper {
    private final CalendarProps calendarProps;

    public List<VEvent> iCalEventsFrom(List<Event> events) {
        return events.stream()
                .map(this::eventToVEvent)
                .toList();
    }

    private VEvent eventToVEvent(Event event) {
        return new VEvent(
                toIcal(event.getStart()),
                toIcal(event.getEnd()),
                event.getEventName()
        );
    }

    private DateTime toIcal(LocalDateTime localDateTime) {
        Instant instant = localDateTime.atZone(calendarProps.zoneId()).toInstant();
        TimeZone timeZone = iCaltimeZone(calendarProps.zoneId().getId());
        return new DateTime(from(instant), timeZone);
    }

    private TimeZone iCaltimeZone(String id) {
        TimeZoneRegistry registry = new CalendarBuilder().getRegistry();
        return registry.getTimeZone(id);
    }
}
