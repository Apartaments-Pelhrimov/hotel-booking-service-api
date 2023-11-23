package ua.mibal.booking.service.photo;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
public enum PhotoExtension {

    PNG, JPG, JPEG;

    public String getExtension() {
        return name().toLowerCase();
    }
}
