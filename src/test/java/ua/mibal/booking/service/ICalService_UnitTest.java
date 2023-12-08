package ua.mibal.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ua.mibal.booking.config.properties.CalendarProps;
import ua.mibal.booking.model.entity.Event;

import java.io.File;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static ua.mibal.booking.testUtils.ICalTestUtils.mustContainEvents;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ICalService.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ICalService_UnitTest {
    private final static ZoneId zoneId = ZoneId.of("Europe/Kyiv");

    @Autowired
    private ICalService service;

    @MockBean
    private CalendarProps calendarProps;

    /**
     * @return random events
     */
    private static List<Event> randomEvents() {
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
     * @return hardcoded events from {@code classpath:test.ics}
     */
    private static List<Event> testEvents() {
        ZoneId australia = ZoneId.of("Australia/Darwin");
        LocalDateTime first = timeAtToOurZoneId(
                LocalDate.of(2024, 1, 1).atStartOfDay(), australia, zoneId);
        LocalDateTime twentyFifth = timeAtToOurZoneId(
                LocalDate.of(2023, 12, 25).atStartOfDay(), australia, zoneId);
        LocalDateTime birthday = timeAtToOurZoneId(
                LocalDate.of(2004, 9, 18).atStartOfDay(), australia, zoneId);
        return List.of(
                Event.from(first, first, "New Year"),
                Event.from(twentyFifth, first, "Christmas holidays"),
                Event.from(birthday, birthday, "My Birthday")
        );
    }

    private static LocalDateTime timeAtToOurZoneId(LocalDateTime localDateTime, ZoneId original, ZoneId wanted) {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, original);
        return zonedDateTime.toOffsetDateTime().atZoneSameInstant(wanted).toLocalDateTime();
    }

    @BeforeEach
    void setup() {
        when(calendarProps.zoneId()).thenReturn(zoneId);
    }

    @Test
    void calendarFromEvents() {
        List<Event> events = randomEvents();

        String calendar = service.calendarFromEvents(events);

        mustContainEvents(calendar, events, zoneId);
    }

    @Test
    void eventsFromFile() throws URISyntaxException {
        File iCalFile = new File(getClass().getClassLoader().getResource("test.ics").toURI());

        List<Event> expected = testEvents();
        List<Event> actual = service.eventsFromFile(iCalFile);

        assertEquals(expected, actual);
    }
}
