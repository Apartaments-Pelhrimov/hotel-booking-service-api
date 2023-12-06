package ua.mibal.booking.model.exception;

import java.time.LocalDateTime;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class ApartmentIsNotAvialableForReservation extends BadRequestException {

    public ApartmentIsNotAvialableForReservation(LocalDateTime from, LocalDateTime to, Long id) {
        super("Apartment with id=" + id + " is not avialable for date " +
              "[" + from.format(ISO_LOCAL_DATE) + ", " + to.format(ISO_LOCAL_DATE) + "]" +
              ".");
    }
}
