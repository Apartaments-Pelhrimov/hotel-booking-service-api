package ua.mibal.booking.model.dto.response.calendar;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.mibal.booking.model.entity.Event;

import java.time.LocalDateTime;
import java.time.Month;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Getter
@Setter
@NoArgsConstructor
public class Range {
    private DateEntry start;
    private DateEntry end;

    public Range(Event event) {
        start = DateEntry.of(event.getStart());
        end = DateEntry.of(event.getEnd());
    }

    public record DateEntry(int day, Month month) {

        public static DateEntry of(LocalDateTime date) {
            Month month = date.getMonth();
            int day = date.getDayOfMonth();
            return new DateEntry(day, month);
        }
    }
}
