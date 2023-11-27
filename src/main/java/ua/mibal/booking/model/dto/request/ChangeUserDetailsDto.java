package ua.mibal.booking.model.dto.request;

import jakarta.validation.constraints.Pattern;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
public record ChangeUserDetailsDto(

        @Pattern(
                regexp = "^(\\p{L}){3,50}",
                message = "must be legal name and should be longer than 3"
        ) String firstName,

        @Pattern(
                regexp = "^(\\p{L}){3,50}",
                message = "must be legal name and should be longer than 3"
        ) String lastName,

        @Pattern(
                regexp = "^\\+([0-9]){10,15}",
                message = "must be valid phone"
        ) String phone
) {
}
