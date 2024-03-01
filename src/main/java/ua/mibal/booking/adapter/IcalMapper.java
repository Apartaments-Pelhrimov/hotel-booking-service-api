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

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.mibal.booking.config.properties.ApplicationProps;
import ua.mibal.booking.domain.Event;

import java.util.List;

import static biweekly.ICalVersion.V2_0;
import static biweekly.property.CalendarScale.gregorian;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Component
public class IcalMapper {
    private final ApplicationProps appProps;
    private final IcalEventMapper icalEventMapper;

    public String toIcal(List<Event> events) {
        ICalendar calendar = initIcal();
        List<VEvent> icalEvents = icalEventMapper.toIcalEvents(events);
        icalEvents.forEach(calendar::addEvent);
        return Biweekly.write(calendar).go();
    }

    public List<Event> getEvents(String icalCalendar) {
        ICalendar calendar = Biweekly.parse(icalCalendar).first();
        List<VEvent> vevents = calendar.getEvents();
        return icalEventMapper.toEvents(vevents);
    }

    private ICalendar initIcal() {
        ICalendar calendar = new ICalendar();
        calendar.setProductId(generateProductId());
        calendar.setVersion(V2_0);
        calendar.setCalendarScale(gregorian());
        return calendar;
    }

    private String generateProductId() {
        return "-//" + appProps.contactLink()
               + "//" + appProps.fullName()
               + "//" + appProps.locale().getCountry();
    }
}
