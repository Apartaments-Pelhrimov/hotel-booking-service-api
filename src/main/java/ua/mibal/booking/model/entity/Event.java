package ua.mibal.booking.model.entity;

import java.time.LocalDateTime;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public interface Event {

    LocalDateTime getStart();

    LocalDateTime getEnd();

    String getEventName();
}
