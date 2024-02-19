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

package ua.mibal.booking.application.dto.response.calendar;

import lombok.NoArgsConstructor;
import ua.mibal.booking.domain.Event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@NoArgsConstructor
public class Calendar extends ArrayList<CalendarEvent> {

    private Calendar(Collection<CalendarEvent> events) {
        super(events);
    }

    public static Calendar of(Collection<Event> events) {
        List<CalendarEvent> calendarEvents = toCalendarEvents(events);
        return new Calendar(calendarEvents);
    }

    private static List<CalendarEvent> toCalendarEvents(Collection<Event> events) {
        return events.stream()
                .map(CalendarEvent::of)
                .toList();
    }
}
