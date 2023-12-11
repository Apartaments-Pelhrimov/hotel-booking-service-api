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

package ua.mibal.booking.service;

import lombok.RequiredArgsConstructor;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Version;
import org.springframework.stereotype.Service;
import ua.mibal.booking.config.properties.CalendarProps;
import ua.mibal.booking.model.entity.Event;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import static ua.mibal.booking.service.util.DateTimeUtils.fromICal;
import static ua.mibal.booking.service.util.DateTimeUtils.toIcal;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class ICalService {
    private final CalendarProps calendarProps;

    public String calendarFromEvents(Collection<Event> events) {
        Calendar calendar = initCalendar();
        List<VEvent> vEvents = eventsToVEvents(events);
        calendar.getComponents().addAll(vEvents);
        return calendar.toString();
    }

    /**
     * Returns list of events from {@link InputStream} calendar file.
     * NOTICE: method closes the {@link InputStream} {@code calendarStream}.
     * @param calendarStream calendar source with events.
     * @return {@link List} of {@link Event}
     */
    public List<Event> eventsFromCalendarStream(InputStream calendarStream) {
        Calendar calendar = calendarFromInputStream(calendarStream);
        List<VEvent> vEvents = calendar.getComponents(Component.VEVENT);
        return eventsFromVEvents(vEvents);
    }

    private Calendar calendarFromInputStream(InputStream file) {
        try (file) {
            return new CalendarBuilder().build(file);
        } catch (IOException | ParserException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private List<Event> eventsFromVEvents(List<VEvent> vEvents) {
        return vEvents.stream().map(vEvent -> Event.from(
                fromICal(vEvent.getStartDate(), calendarProps.zoneId()),
                fromICal(vEvent.getEndDate(), calendarProps.zoneId()),
                vEvent.getSummary().getValue()
        )).toList();
    }

    private List<VEvent> eventsToVEvents(Collection<Event> events) {
        return events.stream().map(event -> new VEvent(
                toIcal(event.getStart(), calendarProps.zoneId()),
                toIcal(event.getEnd(), calendarProps.zoneId()),
                event.getEventName())
        ).toList();
    }

    private Calendar initCalendar() {
        Calendar calendar = new Calendar();
        calendar.getProperties().add(calendarProps.iCal().prodId());
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);
        return calendar;
    }
}
