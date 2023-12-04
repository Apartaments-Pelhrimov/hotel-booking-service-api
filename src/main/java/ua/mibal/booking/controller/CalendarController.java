package ua.mibal.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.mibal.booking.model.dto.response.Calendar;
import ua.mibal.booking.service.CalendarService;

import java.time.YearMonth;
import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/apartments")
public class CalendarController {
    private final CalendarService calendarService;

    @GetMapping("/{id}/calendar")
    public List<Calendar> getCalendarForApartment(@PathVariable Long id,
                                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return calendarService.getCalendarsForApartment(id, month);
    }

    @GetMapping("/instances/{id}/calendar")
    public Calendar getCalendarForApartmentInstance(@PathVariable Long id,
                                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return calendarService.getCalendarForApartmentInstance(id, month);
    }
}
