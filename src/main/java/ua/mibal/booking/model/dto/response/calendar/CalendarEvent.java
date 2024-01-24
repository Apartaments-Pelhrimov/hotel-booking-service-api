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

package ua.mibal.booking.model.dto.response.calendar;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.mibal.booking.model.entity.Event;

import java.time.LocalDateTime;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Getter
@Setter
@NoArgsConstructor
public class CalendarEvent {
    private LocalDateTime start;
    private LocalDateTime end;
    private String name;

    private CalendarEvent(Event event) {
        start = event.getStart();
        end = event.getEnd();
        name = event.getEventName();
    }

    public static CalendarEvent of(Event event) {
        return new CalendarEvent(event);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CalendarEvent range = (CalendarEvent) o;

        if (!start.equals(range.start)) {
            return false;
        }
        if (!end.equals(range.end)) {
            return false;
        }
        return name.equals(range.name);
    }

    @Override
    public int hashCode() {
        int result = start.hashCode();
        result = 31 * result + end.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
