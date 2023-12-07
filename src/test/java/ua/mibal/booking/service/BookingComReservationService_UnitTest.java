package ua.mibal.booking.service;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ua.mibal.booking.config.properties.BookingICalProps;
import ua.mibal.booking.model.entity.ApartmentInstance;
import ua.mibal.booking.model.entity.Event;
import ua.mibal.booking.model.exception.NotFoundException;
import ua.mibal.booking.model.mapper.EventMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BookingComReservationService.class)
@TestPropertySource(locations = "classpath:application.yaml")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestMethodOrder(OrderAnnotation.class)
class BookingComReservationService_UnitTest {

    @Autowired
    private BookingComReservationService service;

    @MockBean
    private BookingICalProps bookingICalProps;

    @Mock
    private ApartmentInstance apartmentInstance;

    @ParameterizedTest
    @Order(1)
    @CsvSource({
            "file:/Users/admin/Downloads/Calendar.ics",
    })
    void getEventsForApartmentInstance(String uri) {
        when(apartmentInstance.getBookingIcalId()).thenReturn(Optional.of(""));
        when(bookingICalProps.baseUrl()).thenReturn("test/file.ics");

        List<? extends Event> actual = service.getEventsForApartmentInstance(apartmentInstance);


    }

    @Test
    @Order(2)
    void getEventsForApartmentInstance_should_throw_NotFoundException() {
        when(apartmentInstance.getBookingIcalId()).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> service.getEventsForApartmentInstance(apartmentInstance)
        );
        assertTrue(e.getMessage().toLowerCase().contains("ical"));
    }

    @ParameterizedTest
    @Order(3)
    @CsvSource({
            "invalid_url\",                  urisyntaxexception",
            "valid_url,                      is not absolute",
            "./valid_url/,                   is not absolute",
            "//valid_url/,                   is not absolute",
            "~/valid_url/,                   is not absolute",
            "file:/notexists/notExists.file, filenotfoundexception",
    })
    void getEventsForApartmentInstance_should_throw_IllegalArgumentException_if_uri_is_invalid(String uri, String message) {
        when(apartmentInstance.getBookingIcalId()).thenReturn(Optional.of("id"));
        when(bookingICalProps.baseUrl()).thenReturn(uri);

        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> service.getEventsForApartmentInstance(apartmentInstance)
        );
        assertTrue(e.getMessage().toLowerCase().contains(message));
    }

    @Test
    @Order(4)
    void getEventsForApartmentInstance_should_throw_IllegalArgumentException_if_file_format_is_invalid() {
        when(apartmentInstance.getBookingIcalId()).thenReturn(Optional.of(""));
        when(bookingICalProps.baseUrl()).thenReturn(
                "file:/Users/admin/IdeaProjects/hotel-booking-service/" +
                "src/test/resources/incorrectCalendar.ics");

        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> service.getEventsForApartmentInstance(apartmentInstance)
        );
        assertTrue(e.getMessage().contains("ParserException"));
    }

    @Test
    void isFree() {

    }
}
