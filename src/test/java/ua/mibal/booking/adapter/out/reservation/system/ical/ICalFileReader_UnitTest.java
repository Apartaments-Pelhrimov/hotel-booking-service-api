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

package ua.mibal.booking.adapter.out.reservation.system.ical;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import ua.mibal.booking.adapter.ICalEventMapper;
import ua.mibal.booking.domain.Event;
import ua.mibal.test.annotation.UnitTest;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static net.fortuna.ical4j.model.Component.VEVENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class ICalFileReader_UnitTest {

    private ICalFileReader reader;

    @Mock
    private ICalEventMapper iCalEventMapper;

    @Mock
    private InputStream inputStream;
    @Mock
    private Calendar calendar;
    @Mock
    private VEvent vEvent;
    @Mock
    private Event event;

    private MockedConstruction<CalendarBuilder> calendarBuilderMockedConstructor;

    @BeforeEach
    void setUp() {
        reader = new ICalFileReader(iCalEventMapper);
    }

    @AfterEach
    void tearDown() {
        calendarBuilderMockedConstructor.close();
    }

    @Test
    void readEventsFromCalendar() {
        calendarBuilderMockedConstructor = mockConstruction(CalendarBuilder.class, (calendarBuilder, context) ->
                when(calendarBuilder.build(inputStream))
                        .thenReturn(calendar));

        when(calendar.getComponents(VEVENT))
                .thenReturn(List.of(vEvent));
        when(iCalEventMapper.toEvents(List.of(vEvent)))
                .thenReturn(List.of(event));

        List<Event> actual = reader.readEventsFromCalendar(inputStream);

        assertThat(actual).containsOnly(event);
    }

    @Test
    void readEventsFromCalendar_should_throw__if_ParserException_was_thrown() {
        calendarBuilderMockedConstructor = mockConstruction(CalendarBuilder.class, (calendarBuilder, context) ->
                when(calendarBuilder.build(inputStream))
                        .thenThrow(ParserException.class));

        assertThrows(ICalEventReaderException.class,
                () -> reader.readEventsFromCalendar(inputStream));
    }

    @Test
    void readEventsFromCalendar_should_throw__if_IOException_was_thrown() {
        calendarBuilderMockedConstructor = mockConstruction(CalendarBuilder.class, (calendarBuilder, context) ->
                when(calendarBuilder.build(inputStream))
                        .thenThrow(IOException.class));

        assertThrows(ICalEventReaderException.class,
                () -> reader.readEventsFromCalendar(inputStream));
    }
}
