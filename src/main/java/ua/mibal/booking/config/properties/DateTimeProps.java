package ua.mibal.booking.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.ZoneId;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@ConfigurationProperties(prefix = "date-time")
public record DateTimeProps(
        ZoneId zoneId
) {
}
