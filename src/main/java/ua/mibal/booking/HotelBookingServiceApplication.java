package ua.mibal.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ua.mibal.booking.config.RsaProperties;

@SpringBootApplication
@EnableConfigurationProperties(RsaProperties.class)
public class HotelBookingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelBookingServiceApplication.class, args);
    }
}
