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
import ua.mibal.booking.config.properties.ApplicationProps;
import ua.mibal.booking.domain.Event;
import ua.mibal.test.annotation.UnitTest;

import java.util.Date;
import java.util.List;

import static java.util.Calendar.SEPTEMBER;
import static java.util.Date.UTC;
import static java.util.Locale.CHINA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class IcalBiweeklyMapper_UnitTest {

    private IcalBiweeklyMapper mapper;

    @Mock
    private ApplicationProps appProps;
    @Mock
    private IcalBiweeklyEventMapper eventMapper;

    @Mock
    private Event stubEvent1;
    @Mock
    private Event stubEvent2;

    @BeforeEach
    void setUp() {
        mapper = new IcalBiweeklyMapper(appProps, eventMapper);
    }

    @Test
    void toIcal() {
        when(appProps.fullName())
                .thenReturn("TEST FULL NAME");
        when(appProps.locale())
                .thenReturn(CHINA);
        when(appProps.contactLink())
                .thenReturn("https://test.contact.link/contacts");

        VEvent event1 = new VEvent();
        event1.setUid("UID1");
        event1.setDateTimeStamp(new Date(UTC(2024 - 1900, SEPTEMBER, 18, 00, 00, 00)));
        event1.setDateStart(new Date(UTC(2004 - 1900, SEPTEMBER, 18, 12, 12, 00)));
        event1.setDateEnd(new Date(UTC(2004 - 1900, SEPTEMBER, 18, 14, 12, 00)));
        event1.setSummary("Mykhailo's Birthday");

        VEvent event2 = new VEvent();
        event2.setUid("UID2");
        event2.setDateTimeStamp(new Date(UTC(2024 - 1900, SEPTEMBER, 18, 00, 00, 00)));
        event2.setDateStart(new Date(UTC(2024 - 1900, SEPTEMBER, 18, 00, 00, 00)));
        event2.setDateEnd(new Date(UTC(2024 - 1900, SEPTEMBER, 18, 23, 59, 00)));
        event2.setSummary("Mykhailo's 20th Birthday");

        when(eventMapper.toIcalEvents(List.of(stubEvent1, stubEvent2)))
                .thenReturn(List.of(event1, event2));

        String actualCal = mapper.toIcal(List.of(stubEvent1, stubEvent2));

        assertEquals("""
                BEGIN:VCALENDAR
                VERSION:2.0
                PRODID:-//https://test.contact.link/contacts//TEST FULL NAME//CN
                CALSCALE:GREGORIAN
                BEGIN:VEVENT
                UID:UID1
                DTSTAMP:20240918T000000Z
                DTSTART:20040918T121200Z
                DTEND:20040918T141200Z
                SUMMARY:Mykhailo's Birthday
                END:VEVENT
                BEGIN:VEVENT
                UID:UID2
                DTSTAMP:20240918T000000Z
                DTSTART:20240918T000000Z
                DTEND:20240918T235900Z
                SUMMARY:Mykhailo's 20th Birthday
                END:VEVENT
                END:VCALENDAR
                """.replaceAll("\n", "\r\n"), actualCal);
    }

    @Test
    void getEvents() {
        String calendar = """
                BEGIN:VCALENDAR
                VERSION:2.0
                PRODID:-//https://test.contact.link/contacts//TEST FULL NAME//CN
                CALSCALE:GREGORIAN
                BEGIN:VEVENT
                UID:UID1
                DTSTAMP:20240918T000000Z
                DTSTART:20040918T121200Z
                DTEND:20040918T141200Z
                SUMMARY:Mykhailo's Birthday
                END:VEVENT
                BEGIN:VEVENT
                UID:UID2
                DTSTAMP:20240918T000000Z
                DTSTART:20240918T000000Z
                DTEND:20240918T235900Z
                SUMMARY:Mykhailo's 20th Birthday
                END:VEVENT
                END:VCALENDAR
                """;

        VEvent event1 = new VEvent();
        event1.setUid("UID1");
        event1.setDateTimeStamp(new Date(UTC(2024 - 1900, SEPTEMBER, 18, 00, 00, 00)));
        event1.setDateStart(new Date(UTC(2004 - 1900, SEPTEMBER, 18, 12, 12, 00)));
        event1.setDateEnd(new Date(UTC(2004 - 1900, SEPTEMBER, 18, 14, 12, 00)));
        event1.setSummary("Mykhailo's Birthday");

        VEvent event2 = new VEvent();
        event2.setUid("UID2");
        event2.setDateTimeStamp(new Date(UTC(2024 - 1900, SEPTEMBER, 18, 00, 00, 00)));
        event2.setDateStart(new Date(UTC(2024 - 1900, SEPTEMBER, 18, 00, 00, 00)));
        event2.setDateEnd(new Date(UTC(2024 - 1900, SEPTEMBER, 18, 23, 59, 00)));
        event2.setSummary("Mykhailo's 20th Birthday");

        when(eventMapper.toEvents(List.of(event1, event2)))
                .thenReturn(List.of(stubEvent1, stubEvent2));

        List<Event> actualEvents = mapper.getEvents(calendar);

        assertThat(actualEvents).containsOnly(stubEvent1, stubEvent2);
    }
}
