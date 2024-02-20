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

package ua.mibal.test.util;

import ua.mibal.booking.domain.Event;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class ICalTestUtils {

    private final static String eventWithTimezoneTemplate = """
            DTSTART;TZID=%s:%s\r
            DTEND;TZID=%s:%s\r
            SUMMARY:%s\r
            END:VEVENT""";

    public static void mustContainEvents(String calendar, List<Event> events, ZoneId zoneId) {
        for (Event event : events) {
            assertTrue(containsEvent(calendar, event, zoneId));
        }
    }

    private static boolean containsEvent(String calendar, Event event, ZoneId zoneId) {
        String eventWithTimezone = eventWithTimezone(event, zoneId);
        return calendar.contains(eventWithTimezone);
    }

    private static String eventWithTimezone(Event event, ZoneId zoneId) {
        DateTimeFormatter iCalFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
        return String.format(
                eventWithTimezoneTemplate,
                zoneId, event.getStart()
                        .format(iCalFormatter),
                zoneId, event.getEnd()
                        .format(iCalFormatter),
                event.getEventName()
        );
    }
}
