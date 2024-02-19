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

package ua.mibal.booking.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.mibal.booking.model.dto.response.calendar.Calendar;
import ua.mibal.booking.application.calendar.CalendarService;

import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class CalendarController {
    private final CalendarService calendarService;

    @GetMapping("/apartments/{id}/calendar")
    public List<Calendar> getCalendarForApartment(@PathVariable Long id) {
        return calendarService.getCalendarsForApartment(id);
    }

    @GetMapping("/apartments/instances/{id}/calendar")
    public Calendar getCalendarForApartmentInstance(@PathVariable Long id) {
        return calendarService.getCalendarForApartmentInstance(id);
    }

    @GetMapping(
            value = "/apartments/instances/{id}/calendar.ics",
            produces = "text/calendar"
    )
    public String getICalForApartmentInstance(@PathVariable Long id) {
        return calendarService.getICalForApartmentInstance(id);
    }
}
