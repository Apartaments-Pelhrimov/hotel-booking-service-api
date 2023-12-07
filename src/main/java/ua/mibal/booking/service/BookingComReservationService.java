package ua.mibal.booking.service;

import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Url;
import org.springframework.stereotype.Service;
import ua.mibal.booking.model.entity.ApartmentInstance;
import ua.mibal.booking.model.entity.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Service
public class BookingComReservationService {

    public boolean isFree(ApartmentInstance apartmentInstance, LocalDateTime start, LocalDateTime end) {
        List<Event> events = getEventsForApartmentInstance(apartmentInstance);
        Predicate<Event> intersectsWithRange =
                ev -> ev.getEnd().isAfter(start) &&
                      ev.getStart().isBefore(end);
        return events.stream().anyMatch(intersectsWithRange);
    }

    public List<Event> getEventsForApartmentInstance(ApartmentInstance apartmentInstance) {
        Url url = linkedUrlById(apartmentInstance.getBookingIcalId());
        List<VEvent> vEvents = vEventsByUrl(url);
        return eventsFromVEvents(vEvents);
    }

    // TODO
    private List<Event> eventsFromVEvents(List<VEvent> vEvents) {
        return null;
    }

    private List<VEvent> vEventsByUrl(Url url) {
        return null;
    }

    private Url linkedUrlById(String bookingIcalId) {
        return null;
    }
}
