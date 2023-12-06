package ua.mibal.booking.model.exception;

import java.time.LocalDateTime;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class IllegalTurningOffTimeException extends BadRequestException {

    public IllegalTurningOffTimeException(LocalDateTime from, LocalDateTime to) {
        super("You can not turn off because there are reservations " +
              "at this date range: [" + from + ", " + to + "]");
    }
}
