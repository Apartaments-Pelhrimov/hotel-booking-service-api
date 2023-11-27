package ua.mibal.booking.model.dto.auth;

import jakarta.validation.constraints.Pattern;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
public record ForgetPasswordDto(

        @Pattern(
                regexp = "^\\S{8,50}",
                message = "must be longer than 8 and without space character"
        ) String password
) {
}
