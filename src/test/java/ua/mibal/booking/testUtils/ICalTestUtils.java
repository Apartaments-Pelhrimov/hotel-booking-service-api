package ua.mibal.booking.testUtils;

import ua.mibal.booking.model.entity.Event;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class ICalTestUtils {

    private final static String eventWithTimezoneTemplate = """
            DTSTART;TZID=%s:%s\r
            DTEND;TZID=%s:%s\r
            SUMMARY:%s\r
            END:VEVENT""";

    public static void mustContainEvents(String calendar, List<Event> events, ZoneId zoneId) {
        for (Event event : events) {
            assertTrue(containsEvent(calendar, event, zoneId));
        }
    }

    private static boolean containsEvent(String calendar, Event event, ZoneId zoneId) {
        String eventWithTimezone = eventWithTimezone(event, zoneId);
        return calendar.contains(eventWithTimezone);
    }

    private static String eventWithTimezone(Event event, ZoneId zoneId) {
        DateTimeFormatter iCalFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
        return String.format(
                eventWithTimezoneTemplate,
                zoneId, event.getStart()
                        .format(iCalFormatter),
                zoneId, event.getEnd()
                        .format(iCalFormatter),
                event.getEventName()
        );
    }
}
