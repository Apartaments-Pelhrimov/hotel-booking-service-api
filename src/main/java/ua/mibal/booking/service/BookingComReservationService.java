package ua.mibal.booking.service;

import org.springframework.stereotype.Service;
import ua.mibal.booking.model.entity.ApartmentInstance;
import ua.mibal.booking.model.entity.Event;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Service
public class BookingComReservationService {

    // TODO
    public boolean isFree(ApartmentInstance apartmentInstance, LocalDateTime from, LocalDateTime to) {
        return true;
    }

    public List<Event> getEventsForApartmentInstance(ApartmentInstance apartmentInstance) {
        return List.of();
    }
}
