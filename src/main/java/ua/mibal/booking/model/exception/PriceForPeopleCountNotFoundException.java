package ua.mibal.booking.model.exception;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class PriceForPeopleCountNotFoundException extends NotFoundException {

    public PriceForPeopleCountNotFoundException(Long apartmentId, Integer people) {
        super("Apartment with id=" + apartmentId + " does not have options for " + people + " people.");
    }
}
