package ua.mibal.booking.model.exception.entity;

import ua.mibal.booking.model.entity.Reservation;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class ReservationNotFoundException extends EntityNotFoundException {

    public ReservationNotFoundException(Long id) {
        super(Reservation.class, id);
    }
}
