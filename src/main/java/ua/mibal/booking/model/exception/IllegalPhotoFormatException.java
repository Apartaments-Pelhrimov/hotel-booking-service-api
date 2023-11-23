package ua.mibal.booking.model.exception;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
public class IllegalPhotoFormatException extends RuntimeException {

    public IllegalPhotoFormatException(String format) {
        super("Illegal photo format: " + format);
    }
}
