package ua.mibal.booking.model.exception;

import java.time.LocalDateTime;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class FreeApartmentsForDateNotFoundException extends NotFoundException {

    public FreeApartmentsForDateNotFoundException(LocalDateTime from, LocalDateTime to, Long id) {
        super("Free Apartment (id=" + id + ") instances for date from=" + from + " to=" + to + " not found.");
    }
}
