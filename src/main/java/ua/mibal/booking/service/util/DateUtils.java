package ua.mibal.booking.service.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.mibal.booking.config.properties.DateTimeProps;

import java.time.ZonedDateTime;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Component
public class DateUtils {
    private final DateTimeProps dateTimeProps;

    public ZonedDateTime now() {
        return null;
    }
}
