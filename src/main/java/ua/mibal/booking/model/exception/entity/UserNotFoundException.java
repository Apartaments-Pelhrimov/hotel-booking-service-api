package ua.mibal.booking.model.exception.entity;

import ua.mibal.booking.model.entity.User;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException(Long id) {
        super(User.class, id);
    }

    public UserNotFoundException(String email) {
        super(email);
    }
}
