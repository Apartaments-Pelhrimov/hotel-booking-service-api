package ua.mibal.booking.model.dto.request;

import java.time.LocalDateTime;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public record TurnOffDto(
        LocalDateTime from,
        LocalDateTime to,
        String event
) {
}
