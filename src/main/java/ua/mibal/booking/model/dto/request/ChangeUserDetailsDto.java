package ua.mibal.booking.model.dto.request;

import ua.mibal.booking.model.validation.constraints.Name;
import ua.mibal.booking.model.validation.constraints.Phone;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
public record ChangeUserDetailsDto(

        @Name
        String firstName,

        @Name
        String lastName,

        @Phone
        String phone
) {
}
