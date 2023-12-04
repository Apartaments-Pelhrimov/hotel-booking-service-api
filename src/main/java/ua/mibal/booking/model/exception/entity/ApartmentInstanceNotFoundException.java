package ua.mibal.booking.model.exception.entity;

import ua.mibal.booking.model.entity.ApartmentInstance;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class ApartmentInstanceNotFoundException extends EntityNotFoundException {

    public ApartmentInstanceNotFoundException(Long apartmentInstanceId) {
        super(ApartmentInstance.class, apartmentInstanceId);
    }
}
