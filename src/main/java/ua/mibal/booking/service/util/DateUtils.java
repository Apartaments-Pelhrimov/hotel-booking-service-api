package ua.mibal.booking.service.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.mibal.booking.config.properties.DateTimeProps;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Component
public class DateUtils {
    private final DateTimeProps dateTimeProps;
    // TODO

    public LocalDateTime reserveFrom(LocalDate from) {
        return null;
    }

    public LocalDateTime reserveTo(LocalDate to) {
        return null;
    }
}
