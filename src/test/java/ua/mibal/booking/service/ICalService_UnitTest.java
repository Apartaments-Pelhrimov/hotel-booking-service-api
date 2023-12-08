package ua.mibal.booking.service;

import net.fortuna.ical4j.model.property.ProdId;
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
import ua.mibal.booking.testUtils.DataGenerator;

import java.io.File;
import java.net.URISyntaxException;
import java.time.ZoneId;
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
    @MockBean
    private CalendarProps.ICalProps iCalProps;

    @BeforeEach
    void setup() {
        when(calendarProps.zoneId()).thenReturn(zoneId);
        when(calendarProps.iCal()).thenReturn(iCalProps);
        when(iCalProps.prodId()).thenReturn(new ProdId("TEST"));
    }

    @Test
    void calendarFromEvents() {
        List<Event> events = DataGenerator.randomEvents();

        String calendar = service.calendarFromEvents(events);

        mustContainEvents(calendar, events, zoneId);
    }

    @Test
    void eventsFromFile() throws URISyntaxException {
        File iCalFile = new File(getClass().getClassLoader().getResource("test.ics").toURI());
        List<Event> expected = DataGenerator.testEventsFromTestFile(zoneId);

        List<Event> actual = service.eventsFromFile(iCalFile);

        assertEquals(expected, actual);
    }
}
