package ua.mibal.booking.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
public record ForgetPasswordDto(

        @NotBlank
        @Size(min = 8, max = 100)
        String password
) {
}
