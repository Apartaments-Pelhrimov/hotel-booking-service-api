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

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import ua.mibal.booking.domain.Event;
import ua.mibal.test.annotation.UnitTest;
import ua.mibal.test.model.TestEvent;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class ICalEventMapper_UnitTest {

    private ICalEventMapper mapper;

    @Mock
    private ICalDateTimeFormatter formatter;

    @BeforeEach
    void setUp() {
        mapper = new ICalEventMapper(formatter);
    }

    @ParameterizedTest
    @InstancioSource
    void toICalEvents(TestEvent event, DateTime vEventStart, DateTime vEventEnd) {
        when(formatter.toIcal(event.getStart()))
                .thenReturn(vEventStart);
        when(formatter.toIcal(event.getEnd()))
                .thenReturn(vEventEnd);

        List<Event> events = List.of(event);

        List<VEvent> actual = mapper.toICalEvents(events);

        assertThat(actual.size()).isOne();
        assertThat(actual.get(0).getSummary().getValue()).isEqualTo(event.getEventName());
        assertThat(actual.get(0).getStartDate().getDate()).isEqualTo(vEventStart);
        assertThat(actual.get(0).getEndDate().getDate()).isEqualTo(vEventEnd);
    }

    @ParameterizedTest
    @MethodSource("ua.mibal.test.util.DataGenerator#testICalVEvents")
    void toEvents(VEvent vEvent, LocalDateTime eventStart, LocalDateTime eventEnd) {
        when(formatter.fromICal(vEvent.getStartDate()))
                .thenReturn(eventStart);
        when(formatter.fromICal(vEvent.getEndDate()))
                .thenReturn(eventEnd);

        List<VEvent> vEvents = List.of(vEvent);

        List<Event> actual = mapper.toEvents(vEvents);

        assertThat(actual.size()).isOne();
        assertThat(actual.get(0).getEventName()).isEqualTo(vEvent.getSummary().getValue());
        assertThat(actual.get(0).getStart()).isEqualTo(eventStart);
        assertThat(actual.get(0).getEnd()).isEqualTo(eventEnd);
    }
}
