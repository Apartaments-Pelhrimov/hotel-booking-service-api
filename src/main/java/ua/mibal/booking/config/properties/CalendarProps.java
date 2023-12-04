package ua.mibal.booking.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.LocalTime;
import java.time.ZoneId;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@ConfigurationProperties("calendar")
public record CalendarProps(
        ZoneId zoneId,
        ReservationDateTimeProps reservationDateTime
) {

    @ConfigurationProperties("calendar.reservation-hours")
    public record ReservationDateTimeProps(
            Integer start,
            Integer end
    ) {
        public LocalTime reservationStart() {
            return LocalTime.of(start, 0);
        }

        public LocalTime reservationEnd() {
            return LocalTime.of(end, 0);
        }
    }
}
