package ua.mibal.booking.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public record ReservationFormRequest(

        @NotNull
        @DateTimeFormat(iso = DATE)
        LocalDate from,

        @NotNull
        @DateTimeFormat(iso = DATE)
        LocalDate to,

        @NotNull
        @Positive
        Integer people
) {
}
