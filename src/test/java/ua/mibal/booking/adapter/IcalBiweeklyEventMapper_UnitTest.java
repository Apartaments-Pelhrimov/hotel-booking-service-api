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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ua.mibal.booking.config.properties.CalendarProps;
import ua.mibal.booking.domain.Event;
import ua.mibal.test.annotation.UnitTest;
import ua.mibal.test.model.TestEvent;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static java.util.Calendar.DECEMBER;
import static java.util.Calendar.SEPTEMBER;
import static java.util.Date.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class IcalBiweeklyEventMapper_UnitTest {
    private final ZoneId zoneId = ZoneId.of("UTC+2");

    private IcalBiweeklyEventMapper mapper;

    @Mock
    private CalendarProps calendarProps;


    @BeforeEach
    void setUp() {
        when(calendarProps.zoneId())
                .thenReturn(zoneId);
        mapper = new IcalBiweeklyEventMapper(calendarProps);
    }

    @Test
    void toIcalEvents() {
        Event christmas = new TestEvent(
                LocalDateTime.of(2023, 12, 24, 23, 59, 00),
                LocalDateTime.of(2023, 12, 25, 00, 00, 00),
                "Christmas but at UTC+2"
        );
        Event newYear = new TestEvent(
                LocalDateTime.of(2023, 12, 31, 23, 59, 00),
                LocalDateTime.of(2024, 01, 01, 00, 00, 00),
                "New year but at UTC+2"
        );

        List<VEvent> actualVevents = mapper.toIcalEvents(List.of(christmas, newYear));

        assertThat(actualVevents).hasSize(2);

        VEvent vEvent1 = actualVevents.get(0);
        assertEquals("Christmas but at UTC+2", vEvent1.getSummary().getValue());
        assertEquals(new Date(Date.UTC(2023 - 1900, DECEMBER, 24, 21, 59, 00)), vEvent1.getDateStart().getValue());
        assertEquals(new Date(Date.UTC(2023 - 1900, DECEMBER, 24, 22, 00, 00)), vEvent1.getDateEnd().getValue());

        VEvent vEvent2 = actualVevents.get(1);
        assertEquals("New year but at UTC+2", vEvent2.getSummary().getValue());
        assertEquals(new Date(Date.UTC(2023 - 1900, DECEMBER, 31, 21, 59, 00)), vEvent2.getDateStart().getValue());
        assertEquals(new Date(Date.UTC(2023 - 1900, DECEMBER, 31, 22, 00, 00)), vEvent2.getDateEnd().getValue());
    }

    @Test
    void toEvents() {
        VEvent vevent1 = new VEvent();
        vevent1.setDateStart(new Date(UTC(2004 - 1900, SEPTEMBER, 18, 12, 12, 00)));
        vevent1.setDateEnd(new Date(UTC(2004 - 1900, SEPTEMBER, 18, 14, 12, 00)));
        vevent1.setSummary("Mykhailo's Birthday but at UTC");

        VEvent vevent2 = new VEvent();
        vevent2.setDateStart(new Date(UTC(2024 - 1900, SEPTEMBER, 18, 00, 00, 00)));
        vevent2.setDateEnd(new Date(UTC(2024 - 1900, SEPTEMBER, 18, 23, 59, 00)));
        vevent2.setSummary("Mykhailo's 20th Birthday but at UTC");

        List<Event> actualEvents = mapper.toEvents(List.of(vevent1, vevent2));

        assertThat(actualEvents).hasSize(2);

        Event event1 = actualEvents.get(0);
        assertEquals("Mykhailo's Birthday but at UTC", event1.getEventName());
        assertEquals(LocalDateTime.of(2004, 9, 18, 14, 12), event1.getStart());
        assertEquals(LocalDateTime.of(2004, 9, 18, 16, 12), event1.getEnd());

        Event event2 = actualEvents.get(1);
        assertEquals("Mykhailo's 20th Birthday but at UTC", event2.getEventName());
        assertEquals(LocalDateTime.of(2024, 9, 18, 02, 00), event2.getStart());
        assertEquals(LocalDateTime.of(2024, 9, 19, 01, 59), event2.getEnd());
    }

    @Test
    void toEvents_with_zoned_date() {
        VEvent vevent1 = new VEvent();
        vevent1.setDateStart(new Date(UTC(2004 - 1900, SEPTEMBER, 18, 12, 12, 00)));
        vevent1.setDateEnd(new Date(UTC(2004 - 1900, SEPTEMBER, 18, 14, 12, 00)));
        vevent1.setSummary("Mykhailo's Birthday but at UTC");

        VEvent vevent2 = new VEvent();
        vevent2.setDateStart(new Date(UTC(2024 - 1900, SEPTEMBER, 18, 00, 00, 00)));
        vevent2.setDateEnd(new Date(UTC(2024 - 1900, SEPTEMBER, 18, 23, 59, 00)));
        vevent2.setSummary("Mykhailo's 20th Birthday but at UTC");

        List<Event> actualEvents = mapper.toEvents(List.of(vevent1, vevent2));

        assertThat(actualEvents).hasSize(2);

        Event event1 = actualEvents.get(0);
        assertEquals("Mykhailo's Birthday but at UTC", event1.getEventName());
        assertEquals(LocalDateTime.of(2004, 9, 18, 14, 12), event1.getStart());
        assertEquals(LocalDateTime.of(2004, 9, 18, 16, 12), event1.getEnd());

        Event event2 = actualEvents.get(1);
        assertEquals("Mykhailo's 20th Birthday but at UTC", event2.getEventName());
        assertEquals(LocalDateTime.of(2024, 9, 18, 02, 00), event2.getStart());
        assertEquals(LocalDateTime.of(2024, 9, 19, 01, 59), event2.getEnd());
    }
}
