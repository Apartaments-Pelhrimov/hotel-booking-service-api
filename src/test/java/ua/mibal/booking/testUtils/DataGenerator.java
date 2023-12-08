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

package ua.mibal.booking.testUtils;

import org.junit.jupiter.params.provider.Arguments;
import ua.mibal.booking.model.entity.Event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;

import static java.util.List.of;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class DataGenerator {

    /**
     * @return random events
     */
    public static List<Event> randomEvents() {
        LocalDateTime first = LocalDate.of(2024, 1, 1).atStartOfDay();
        LocalDateTime twentyFifth = LocalDate.of(2023, 12, 25).atStartOfDay();
        LocalDateTime birthday = LocalDate.of(2004, 9, 18).atStartOfDay();
        return List.of(
                Event.from(first, first, "New Year"),
                Event.from(twentyFifth, first, "Christmas holidays"),
                Event.from(birthday, birthday, "My Birthday")
        );
    }

    /**
     * @param targetZoneId target wanted to convert {@link ZoneId}
     * @return hardcoded events from {@code classpath:test.ics}
     */
    public static List<Event> testEventsFromTestFile(ZoneId targetZoneId) {
        ZoneId australia = ZoneId.of("Australia/Sydney");
        LocalDateTime first = timeAtToOurZoneId(
                LocalDate.of(2024, 1, 1).atStartOfDay(), australia, targetZoneId);
        LocalDateTime twentyFifth = timeAtToOurZoneId(
                LocalDate.of(2023, 12, 25).atStartOfDay(), ZoneOffset.UTC, targetZoneId);
        LocalDateTime birthday = timeAtToOurZoneId(
                LocalDate.of(2004, 9, 18).atStartOfDay(), australia, targetZoneId);
        return List.of(
                Event.from(first, first, "New Year"),
                Event.from(twentyFifth, first, "Christmas holidays"),
                Event.from(birthday, birthday, "My Birthday")
        );
    }

    /**
     * @return random {@link List} of {@link Event} Apartment reservations and
     *         user intent reservation intervals with expected result of operation
     */
    public static Stream<Arguments> eventsFactory() {
        LocalDateTime first = LocalDate.of(2023, 12, 1).atStartOfDay();
        LocalDateTime third = LocalDate.of(2023, 12, 3).atStartOfDay();
        LocalDateTime fifth = LocalDate.of(2023, 12, 5).atStartOfDay();
        LocalDateTime sixth = LocalDate.of(2023, 12, 6).atStartOfDay();
        return Stream.of(
                Arguments.of(of(), first, fifth, true),
                Arguments.of(of(Event.from(first, third, "")), fifth, sixth, true),
                Arguments.of(of(Event.from(first, fifth, "")), first, fifth, false),
                Arguments.of(of(Event.from(first, third, "")), first, fifth, false),
                Arguments.of(of(Event.from(third, fifth, "")), first, fifth, false)
        );
    }

    private static LocalDateTime timeAtToOurZoneId(LocalDateTime localDateTime, ZoneId original, ZoneId wanted) {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, original);
        return zonedDateTime.toOffsetDateTime().atZoneSameInstant(wanted).toLocalDateTime();
    }
}
