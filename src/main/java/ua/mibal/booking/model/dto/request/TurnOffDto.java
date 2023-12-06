package ua.mibal.booking.model.dto.request;

import ua.mibal.booking.model.validation.constraints.ValidDateRange;

import java.time.LocalDateTime;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@ValidDateRange
public record TurnOffDto(
        LocalDateTime from,
        LocalDateTime to,
        String event
) {
}
