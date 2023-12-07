package ua.mibal.booking.service;

import lombok.RequiredArgsConstructor;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.VEvent;
import org.springframework.stereotype.Service;
import ua.mibal.booking.config.properties.BookingICalProps;
import ua.mibal.booking.model.entity.ApartmentInstance;
import ua.mibal.booking.model.entity.Event;
import ua.mibal.booking.model.exception.NotFoundException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class BookingComReservationService {
    private final BookingICalProps bookingICalProps;


    public boolean isFree(ApartmentInstance apartmentInstance, LocalDateTime start, LocalDateTime end) {
        List<Event> events = getEventsForApartmentInstance(apartmentInstance);
        Predicate<Event> intersectsWithRange =
                ev -> ev.getEnd().isAfter(start) &&
                      ev.getStart().isBefore(end);
        return events.stream().anyMatch(intersectsWithRange);
    }

    public List<Event> getEventsForApartmentInstance(ApartmentInstance apartmentInstance) {
        URI uri = iCalUrlByApartmentInstance(apartmentInstance);
        List<VEvent> vEvents = vEventsByUri(uri);
        return eventsFromVEvents(vEvents);
    }

    // TODO
    private List<Event> eventsFromVEvents(List<VEvent> vEvents) {
        return null;
    }

    private List<VEvent> vEventsByUri(URI uri) {
        try (FileInputStream inputStream = new FileInputStream(new File(uri))) {
            Calendar calendar = new CalendarBuilder().build(inputStream);
            return calendar.getComponents(Component.VEVENT);
        } catch (IOException | ParserException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private URI iCalUrlByApartmentInstance(ApartmentInstance apartmentInstance) {
        try {
            String iCalId = apartmentInstance.getBookingIcalId()
                    .orElseThrow(() -> new NotFoundException(
                            "Apartment instance has not id for booking iCal system"));
            return new URI(bookingICalProps.baseUrl() + iCalId);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
