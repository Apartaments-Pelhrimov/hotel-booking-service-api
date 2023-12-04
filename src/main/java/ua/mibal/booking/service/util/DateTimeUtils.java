package ua.mibal.booking.service.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.mibal.booking.config.properties.ReservationDateTimeProps;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
}
