package ua.mibal.booking.service.util;

import lombok.RequiredArgsConstructor;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.property.DateProperty;
import org.springframework.stereotype.Component;
import ua.mibal.booking.config.properties.CalendarProps.ReservationDateTimeProps;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static java.util.Date.from;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Component
public class DateTimeUtils {
    private final ReservationDateTimeProps reservationDateTimeProps;

    public static LocalDateTime monthStartWithTime(YearMonth yearMonth) {
        return yearMonth.atDay(1).atStartOfDay();
    }

    public static LocalDateTime monthEndWithTime(YearMonth yearMonth) {
        return yearMonth.atEndOfMonth().atTime(23, 59);
    }

    public static DateTime toIcal(LocalDateTime localDateTime, ZoneId sourceZoneId) {
        Instant instant = localDateTime.atZone(sourceZoneId).toInstant();
        TimeZone timeZone = iCaltimeZone(sourceZoneId.getId());
        return new DateTime(from(instant), timeZone);
    }

    private static TimeZone iCaltimeZone(String id) {
        TimeZoneRegistry registry = new CalendarBuilder().getRegistry();
        return registry.getTimeZone(id);
    }

    public static LocalDateTime fromICal(DateProperty dateProperty, ZoneId targetZoneId) {
        Instant instant = dateProperty.getDate().toInstant();
        ZonedDateTime zonedDateTimeAtOurZone = instant.atZone(targetZoneId);
        return zonedDateTimeAtOurZone.toLocalDateTime();
    }

    public LocalDateTime reserveFrom(LocalDate from) {
        return LocalDateTime.of(from, reservationDateTimeProps.reservationStart());
    }

    public LocalDateTime reserveTo(LocalDate to) {
        return LocalDateTime.of(to, reservationDateTimeProps.reservationEnd());
    }
}
