package ua.mibal.booking.service.util;

import org.springframework.util.StringUtils;
import ua.mibal.booking.model.exception.IllegalPhotoFormatException;
import ua.mibal.booking.service.photo.PhotoExtension;

import java.util.Objects;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class FileNameUtils {

    public static PhotoExtension getPhotoExtension(String fileName) {
        String extension = null;
        try {
            extension = StringUtils.getFilenameExtension(fileName);
            return PhotoExtension.valueOf(Objects.requireNonNull(extension).toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalPhotoFormatException(extension);
        }
    }
}
