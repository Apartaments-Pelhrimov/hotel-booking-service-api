package ua.mibal.booking.model.dto.auth;

import ua.mibal.booking.model.validation.constraints.Password;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
public record ForgetPasswordDto(@Password String password) {
}