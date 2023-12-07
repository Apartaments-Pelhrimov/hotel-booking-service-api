package ua.mibal.booking.service;

import lombok.RequiredArgsConstructor;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import org.springframework.stereotype.Service;
import ua.mibal.booking.config.properties.CalendarProps;
import ua.mibal.booking.model.entity.Event;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class ICalService {
    private final CalendarProps calendarProps;

    public String calendarFromEvents(Collection<Event> events) {
        Calendar calendar = initCalendar();
        List<VEvent> vEvents = eventsToVEvents(events);
        calendar.getComponents().addAll(vEvents);
        return calendar.toString();
    }

    public List<Event> eventsFromFile(File file) {
        Calendar calendar = calendarFromFile(file);
        List<VEvent> vEvents = calendar.getComponents(Component.VEVENT);
        return eventsFromVEvents(vEvents);
    }

    private Calendar calendarFromFile(File file) {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            return new CalendarBuilder().build(inputStream);
        } catch (IOException | ParserException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private List<Event> eventsFromVEvents(List<VEvent> vEvents) {
        return vEvents.stream().map(vEvent -> {
            LocalDateTime from = LocalDateTime.from(vEvent.getStartDate().getDate().toInstant());
            LocalDateTime to = LocalDateTime.from(vEvent.getEndDate().getDate().toInstant());
            String eventName = vEvent.getDescription().getValue();
            return Event.from(from, to, eventName);
        }).toList();
    }

    private List<VEvent> eventsToVEvents(Collection<Event> events) {
        return events.stream()
                .map(this::toVEvent)
                .toList();
    }

    private Calendar initCalendar() {
        Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);
        return calendar;
    }

    private VEvent toVEvent(Event event) {
        java.util.Date from = dateFromLocalDateTime(event.getStart());
        java.util.Date to = dateFromLocalDateTime(event.getEnd());
        return new VEvent(
                new DateTime(from),
                new DateTime(to),
                event.getEventName()
        );
    }

    private java.util.Date dateFromLocalDateTime(LocalDateTime localDateTime) {
        Instant instant = localDateTime.atZone(calendarProps.zoneId()).toInstant();
        return java.util.Date.from(instant);
    }
}
