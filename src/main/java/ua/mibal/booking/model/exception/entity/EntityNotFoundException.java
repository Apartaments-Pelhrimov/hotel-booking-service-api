package ua.mibal.booking.model.exception.entity;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public abstract class EntityNotFoundException extends jakarta.persistence.EntityNotFoundException {

    protected final static String MESSAGE_FORMAT = "Entity %s by id=%s not found";

    protected EntityNotFoundException(Class<?> entity, Long id) {
        super(String.format(MESSAGE_FORMAT, entity.getSimpleName(), id));
    }

    protected EntityNotFoundException(String userEmail) {
        super("Entity User by email='" + userEmail + "' not found");
    }
}
