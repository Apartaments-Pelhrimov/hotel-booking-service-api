package ua.mibal.booking.testUtils;

import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import ua.mibal.booking.model.dto.auth.RegistrationDto;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
public class RegistrationDtoArgumentConverter extends SimpleArgumentConverter {

    @Override
    protected RegistrationDto convert(Object source, Class<?> targetType) throws ArgumentConversionException {
        if (source == null) {
            return null;
        }
        if (source instanceof String s) {
            String[] args = s.split(" ");
            return registrationDtoByArgs(args);
        }
        throw new IllegalArgumentException("Conversion from " + source.getClass() + " to "
                                           + targetType + " not supported.");
    }

    private RegistrationDto registrationDtoByArgs(String[] args) {
        return new RegistrationDto(args[0], args[1], args[2], args[3], args[4]);
    }
}
