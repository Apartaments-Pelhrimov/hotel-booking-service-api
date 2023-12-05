package ua.mibal.booking.model.exception;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public abstract class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
