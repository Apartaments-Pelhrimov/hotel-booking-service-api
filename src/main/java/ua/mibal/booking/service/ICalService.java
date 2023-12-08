package ua.mibal.booking.service;

import lombok.RequiredArgsConstructor;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import org.springframework.stereotype.Service;
import ua.mibal.booking.config.properties.CalendarProps;
import ua.mibal.booking.model.entity.Event;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class ICalService {
    private final CalendarProps calendarProps;

    private static LocalDateTime timeAtToOurZoneId(LocalDateTime localDateTime, ZoneId original, ZoneId wanted) {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, original);
        return zonedDateTime.toOffsetDateTime().atZoneSameInstant(wanted).toLocalDateTime();
    }

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
            LocalDateTime from = localDateTimeFrom(vEvent.getStartDate());
            LocalDateTime to = localDateTimeFrom(vEvent.getEndDate());
            String eventName = vEvent.getSummary().getValue();
            return Event.from(from, to, eventName);
        }).toList();
    }

    private List<VEvent> eventsToVEvents(Collection<Event> events) {
        return events.stream().map(event -> new VEvent(
                iCalDateTimeFrom(event.getStart()),
                iCalDateTimeFrom(event.getEnd()),
                event.getEventName())
        ).toList();
    }

    private Calendar initCalendar() {
        Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);
        return calendar;
    }

    private DateTime iCalDateTimeFrom(LocalDateTime localDateTime) {
        String iCalDateString = toICalDateString(localDateTime);
        TimeZoneRegistry registry = new CalendarBuilder().getRegistry();
        TimeZone timeZone = registry.getTimeZone(calendarProps.zoneId().getId());
        try {
            return new DateTime(iCalDateString, timeZone);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private LocalDateTime localDateTimeFrom(DateProperty dateProperty) {
        Date date = dateProperty.getDate();
        String timeZone = dateProperty.getTimeZone().getID();
        LocalDateTime localDateTime = LocalDateTime.parse(date.toString(), ofPattern("yyyyMMdd'T'HHmmss"));
        return timeAtToOurZoneId(localDateTime, ZoneId.of(timeZone), calendarProps.zoneId());
    }

    private String toICalDateString(LocalDateTime localDateTime) {
        return localDateTime.format(ofPattern("yyyyMMdd'T'HHmmss"));
    }
}
