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

import lombok.RequiredArgsConstructor;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.VEvent;
import org.springframework.stereotype.Service;
import ua.mibal.booking.adapter.ICalEventMapper;
import ua.mibal.booking.domain.Event;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class ICalFileReader {
    private final ICalEventMapper iCalEventMapper;

    /**
     * Returns {@link List} of {@link Event}s from {@link InputStream} calendar file.
     * NOTICE: method does NOT CLOSE the {@link InputStream} {@code calendarStream}.
     *
     * @param calendarStream calendar source file with events.
     * @return {@link List} of {@link Event}
     */
    public List<Event> readEventsFromCalendar(InputStream calendarStream) {
        Calendar calendar = buildCalendarFromInputStream(calendarStream);
        List<VEvent> vEvents = calendar.getComponents(Component.VEVENT);
        return iCalEventMapper.toEvents(vEvents);
    }

    private Calendar buildCalendarFromInputStream(InputStream file) {
        try {
            return new CalendarBuilder().build(file);
        } catch (IOException | ParserException | RuntimeException e) {
            throw new ICalEventReaderException(
                    "Exception while building Calendar from ICal file", e
            );
        }
    }
}