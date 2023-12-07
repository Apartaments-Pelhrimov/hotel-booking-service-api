package ua.mibal.booking.model.entity;

import ua.mibal.booking.model.dto.response.SimpleEvent;

import java.time.LocalDateTime;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public interface Event {

    LocalDateTime getStart();

    LocalDateTime getEnd();

    String getEventName();

    static Event from(LocalDateTime from, LocalDateTime to, String eventName) {
        return new SimpleEvent(from, to, eventName);
    }
}
