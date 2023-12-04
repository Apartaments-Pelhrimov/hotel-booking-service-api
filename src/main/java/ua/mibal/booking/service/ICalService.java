package ua.mibal.booking.service;

import lombok.RequiredArgsConstructor;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import org.springframework.stereotype.Service;
import ua.mibal.booking.config.properties.CalendarProps;
import ua.mibal.booking.model.entity.Reservation;
import ua.mibal.booking.model.entity.embeddable.ReservationDetails;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class ICalService {
    private final CalendarProps calendarProps;

    public Calendar calendarFromReservations(List<Reservation> reservations) {
        Calendar calendar = initCalendar();
        List<VEvent> events = reservationsToEvents(reservations);
        calendar.getComponents().addAll(events);
        return calendar;
    }

    private List<VEvent> reservationsToEvents(List<Reservation> reservations) {
        return reservations.stream()
                .map(this::toEvent)
                .toList();
    }

    private Calendar initCalendar() {
        Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);
        return calendar;
    }

    private VEvent toEvent(Reservation reservation) {
        ReservationDetails details = reservation.getDetails();
        java.util.Date from = dateFromLocalDateTime(details.getReservedFrom());
        java.util.Date to = dateFromLocalDateTime(details.getReservedTo());
        return new VEvent(
                new DateTime(from),
                new DateTime(to),
                reservation.getApartmentInstance().getName() + " reservation"
        );
    }

    private java.util.Date dateFromLocalDateTime(LocalDateTime localDateTime) {
        Instant instant = localDateTime.atZone(calendarProps.zoneId()).toInstant();
        return java.util.Date.from(instant);
    }
}
