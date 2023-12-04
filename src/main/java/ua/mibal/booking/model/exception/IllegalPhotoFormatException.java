package ua.mibal.booking.model.exception;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class IllegalPhotoFormatException extends IllegalArgumentException {

    public IllegalPhotoFormatException(String format) {
        super("Illegal photo format: " + format);
    }
}
