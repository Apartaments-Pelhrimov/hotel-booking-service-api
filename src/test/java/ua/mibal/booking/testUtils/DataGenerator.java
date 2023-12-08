package ua.mibal.booking.testUtils;

import org.junit.jupiter.params.provider.Arguments;
import ua.mibal.booking.model.entity.Event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;

import static java.util.List.of;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class DataGenerator {

    /**
     * @return random events
     */
    public static List<Event> randomEvents() {
        LocalDateTime first = LocalDate.of(2024, 1, 1).atStartOfDay();
        LocalDateTime twentyFifth = LocalDate.of(2023, 12, 25).atStartOfDay();
        LocalDateTime birthday = LocalDate.of(2004, 9, 18).atStartOfDay();
        return List.of(
                Event.from(first, first, "New Year"),
                Event.from(twentyFifth, first, "Christmas holidays"),
                Event.from(birthday, birthday, "My Birthday")
        );
    }

    /**
     * @param targetZoneId target wanted to convert {@link ZoneId}
     * @return hardcoded events from {@code classpath:test.ics}
     */
    public static List<Event> testEventsFromTestFile(ZoneId targetZoneId) {
        ZoneId australia = ZoneId.of("Australia/Sydney");
        LocalDateTime first = timeAtToOurZoneId(
                LocalDate.of(2024, 1, 1).atStartOfDay(), australia, targetZoneId);
        LocalDateTime twentyFifth = timeAtToOurZoneId(
                LocalDate.of(2023, 12, 25).atStartOfDay(), ZoneOffset.UTC, targetZoneId);
        LocalDateTime birthday = timeAtToOurZoneId(
                LocalDate.of(2004, 9, 18).atStartOfDay(), australia, targetZoneId);
        return List.of(
                Event.from(first, first, "New Year"),
                Event.from(twentyFifth, first, "Christmas holidays"),
                Event.from(birthday, birthday, "My Birthday")
        );
    }

    /**
     * @return random {@link List} of {@link Event} Apartment reservations and
     *         user intent reservation intervals with expected result of operation
     */
    public static Stream<Arguments> eventsFactory() {
        LocalDateTime first = LocalDate.of(2023, 12, 1).atStartOfDay();
        LocalDateTime third = LocalDate.of(2023, 12, 3).atStartOfDay();
        LocalDateTime fifth = LocalDate.of(2023, 12, 5).atStartOfDay();
        LocalDateTime sixth = LocalDate.of(2023, 12, 6).atStartOfDay();
        return Stream.of(
                Arguments.of(of(), first, fifth, true),
                Arguments.of(of(Event.from(first, third, "")), fifth, sixth, true),
                Arguments.of(of(Event.from(first, fifth, "")), first, fifth, false),
                Arguments.of(of(Event.from(first, third, "")), first, fifth, false),
                Arguments.of(of(Event.from(third, fifth, "")), first, fifth, false)
        );
    }

    private static LocalDateTime timeAtToOurZoneId(LocalDateTime localDateTime, ZoneId original, ZoneId wanted) {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, original);
        return zonedDateTime.toOffsetDateTime().atZoneSameInstant(wanted).toLocalDateTime();
    }
}
