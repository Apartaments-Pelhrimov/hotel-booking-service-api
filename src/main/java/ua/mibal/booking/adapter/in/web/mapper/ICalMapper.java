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

package ua.mibal.booking.adapter.in.web.mapper;

import lombok.RequiredArgsConstructor;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import org.springframework.stereotype.Component;
import ua.mibal.booking.adapter.ICalEventMapper;
import ua.mibal.booking.config.properties.CalendarProps.ICalProps;
import ua.mibal.booking.domain.Event;

import java.util.List;

import static net.fortuna.ical4j.model.property.CalScale.GREGORIAN;
import static net.fortuna.ical4j.model.property.Version.VERSION_2_0;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Component
public class ICalMapper {
    private final ICalProps iCalProps;
    private final ICalEventMapper iCalEventMapper;

    public String toICal(List<Event> events) {
        Calendar calendar = initCalendar();
        List<VEvent> iCalEvents = iCalEventMapper.toICalEvents(events);
        calendar.getComponents().addAll(iCalEvents);
        return calendar.toString();
    }

    private Calendar initCalendar() {
        Calendar calendar = new Calendar();
        calendar.getProperties().add(iCalProps.prodId());
        calendar.getProperties().add(VERSION_2_0);
        calendar.getProperties().add(GREGORIAN);
        return calendar;
    }
}
