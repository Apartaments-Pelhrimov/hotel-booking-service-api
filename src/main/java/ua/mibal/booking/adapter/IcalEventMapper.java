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

package ua.mibal.booking.adapter;

import biweekly.component.VEvent;
import biweekly.property.DateOrDateTimeProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.mibal.booking.config.properties.CalendarProps;
import ua.mibal.booking.domain.DefaultEvent;
import ua.mibal.booking.domain.Event;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Component
public class IcalEventMapper {
    private final CalendarProps calendarProps;

    public List<VEvent> toIcalEvents(List<Event> events) {
        return events.stream()
                .map(this::toIcalEvent)
                .toList();
    }

    public List<Event> toEvents(List<VEvent> vevents) {
        return vevents.stream()
                .map(this::toEvent)
                .toList();
    }

    private VEvent toIcalEvent(Event event) {
        VEvent vevent = new VEvent();
        vevent.setDateStart(toUtcDate(event.getStart()));
        vevent.setDateEnd(toUtcDate(event.getEnd()));
        vevent.setSummary(event.getEventName());
        return vevent;
    }

    private Event toEvent(VEvent vevent) {
        DefaultEvent event = new DefaultEvent();
        event.setEventName(vevent.getSummary().getValue());
        event.setStart(toLocalDate(vevent.getDateStart()));
        event.setEnd(toLocalDate(vevent.getDateEnd()));
        return event;
    }

    private Date toUtcDate(LocalDateTime local) {
        return Date.from(local.atZone(calendarProps.zoneId()).toInstant());
    }

    private LocalDateTime toLocalDate(DateOrDateTimeProperty utcDate) {
        return LocalDateTime.ofInstant(utcDate.getValue().toInstant(), calendarProps.zoneId());
    }
}
