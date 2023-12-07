package ua.mibal.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ua.mibal.booking.config.properties.CalendarProps;
import ua.mibal.booking.model.entity.Event;
import ua.mibal.booking.testUtils.IcalTestUtils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ICalService.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ICalService_UnitTest {
    @Autowired
    private ICalService service;

    @MockBean
    private CalendarProps calendarProps;

    public static Stream<Arguments> events() {
        LocalDateTime first = LocalDate.of(2024, 1, 1).atStartOfDay();
        LocalDateTime twentyFifth = LocalDate.of(2023, 12, 25).atStartOfDay();
        LocalDateTime birthday = LocalDate.of(2004, 9, 18).atStartOfDay();
        return Stream.of(
                Arguments.of(List.of(Event.from(first, first, "New Year"))),
                Arguments.of(List.of(Event.from(twentyFifth, first, "Christmas holidays"))),
                Arguments.of(List.of(Event.from(birthday, birthday, "My Birthday")))
        );
    }

    @BeforeEach
    void setup() {
        when(calendarProps.zoneId())
                .thenReturn(ZoneId.of("Europe/Prague"));
    }

    @ParameterizedTest
    @MethodSource("events")
    void calendarFromEvents(Collection<Event> events) {
        String calendar = service.calendarFromEvents(events);

        List<Event> actual = IcalTestUtils.readEventsFromCalendar(calendar);
        assertEquals(events, actual);
    }

    @Test
    void eventsFromFile() throws URISyntaxException {
        File file = new File(new URI(
                "file:/Users/admin/IdeaProjects/hotel-booking-service/src/test/resources/test.ics"
        ));
        List<Event> expected = IcalTestUtils.writeRandomEventsInFile(file);

        List<Event> actual = service.eventsFromFile(file);

        assertEquals(expected, actual);
    }
}
