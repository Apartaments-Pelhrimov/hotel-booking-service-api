package ua.mibal.booking.repository.custom;

import ua.mibal.booking.model.entity.User;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public interface CustomUserRepository {

    User getReferenceByEmail(String email);
}
