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

import lombok.RequiredArgsConstructor;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.property.DateProperty;
import org.springframework.stereotype.Component;
import ua.mibal.booking.config.properties.CalendarProps;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static java.util.Date.from;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Component
public class ICalDateTimeFormatter {
    private final CalendarProps calendarProps;

    public LocalDateTime fromICal(DateProperty dateProperty) {
        Instant instant = dateProperty.getDate().toInstant();
        ZonedDateTime zonedDateTimeAtOurZone = instant.atZone(calendarProps.zoneId());
        return zonedDateTimeAtOurZone.toLocalDateTime();
    }

    public DateTime toIcal(LocalDateTime localDateTime) {
        Instant instant = localDateTime.atZone(calendarProps.zoneId()).toInstant();
        TimeZone timeZone = iCaltimeZone();
        return new DateTime(from(instant), timeZone);
    }

    private TimeZone iCaltimeZone() {
        TimeZoneRegistry registry = new CalendarBuilder().getRegistry();
        return registry.getTimeZone(calendarProps.zoneId().getId());
    }
}
