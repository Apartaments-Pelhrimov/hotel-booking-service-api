package ua.mibal.booking.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.mibal.booking.model.dto.response.calendar.Calendar;
import ua.mibal.booking.model.request.TurnOffDatesDto;
import ua.mibal.booking.service.CalendarService;

import java.time.YearMonth;
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
    public List<Calendar> getCalendarForApartment(@PathVariable Long id,
                                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return calendarService.getCalendarsForApartment(id, month);
    }

    @GetMapping("/apartments/instances/{id}/calendar")
    public Calendar getCalendarForApartmentInstance(@PathVariable Long id,
                                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return calendarService.getCalendarForApartmentInstance(id, month);
    }

    @GetMapping(
            value = "/apartments/instances/{id}/calendar.ics",
            produces = "text/calendar"
    )
    public String getICalForApartmentInstance(@PathVariable Long id) {
        return calendarService.getICalForApartmentInstance(id);
    }

    @RolesAllowed("MANAGER")
    @PatchMapping("/hotel/off")
    public void turnOffHotel(@Valid @RequestBody TurnOffDatesDto turnOffDatesDto) {
        calendarService.turnOffHotel(turnOffDatesDto);
    }

    @RolesAllowed("MANAGER")
    @PatchMapping("/apartments/instances/{id}/off")
    public void turnOffApartmentInstance(@PathVariable Long id,
                                         @Valid @RequestBody TurnOffDatesDto turnOffDatesDto) {
        calendarService.turnOffApartmentInstance(id, turnOffDatesDto);
    }
}
