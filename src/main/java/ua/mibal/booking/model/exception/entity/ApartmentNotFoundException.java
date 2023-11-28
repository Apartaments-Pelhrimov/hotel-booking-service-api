package ua.mibal.booking.model.exception.entity;

import ua.mibal.booking.model.entity.Apartment;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
public class ApartmentNotFoundException extends EntityNotFoundException {

    public ApartmentNotFoundException(Long id) {
        super(Apartment.class, id);
    }
}
