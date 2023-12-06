package ua.mibal.booking.service.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.mibal.booking.config.properties.CalendarProps.ReservationDateTimeProps;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Component
public class DateTimeUtils {
    private final ReservationDateTimeProps reservationDateTimeProps;

    public LocalDateTime reserveFrom(LocalDate from) {
        return LocalDateTime.of(from, reservationDateTimeProps.reservationStart());
    }

    public LocalDateTime reserveTo(LocalDate to) {
        return LocalDateTime.of(to, reservationDateTimeProps.reservationEnd());
    }

    public LocalDateTime monthStartWithTime(YearMonth yearMonth) {
        return monthStart(yearMonth).atStartOfDay();
    }

    public LocalDate monthStart(YearMonth yearMonth) {
        return yearMonth.atDay(1);
    }

    public LocalDateTime monthEndWithTime(YearMonth yearMonth) {
        return monthEnd(yearMonth).atTime(23, 59);
    }

    public LocalDate monthEnd(YearMonth yearMonth) {
        return yearMonth.atEndOfMonth();
    }
}
