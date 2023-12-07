package ua.mibal.booking.service;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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

    @Test
    @Order(1)
    void getEventsForApartmentInstance() {
        // TODO
        List<Event> actual = service.getEventsForApartmentInstance(apartmentInstance);
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

    @Test
    @Order(3)
    void getEventsForApartmentInstance_should_throw_IllegalArgumentException() {
        when(apartmentInstance.getBookingIcalId()).thenReturn(Optional.of("id"));
        when(bookingICalProps.baseUrl()).thenReturn("invalid_url\"");

        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> service.getEventsForApartmentInstance(apartmentInstance)
        );
        assertTrue(e.getMessage().contains("url"));
    }

    @Test
    @Order(4)
    void isFree() {

    }
}
