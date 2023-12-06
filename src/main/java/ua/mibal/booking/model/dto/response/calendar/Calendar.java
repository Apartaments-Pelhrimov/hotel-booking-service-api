package ua.mibal.booking.model.dto.response.calendar;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.mibal.booking.model.entity.Event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Getter
@Setter
@NoArgsConstructor
public class Calendar extends ArrayList<Range> {
    private Calendar(List<Range> ranges) {
        super(ranges);
    }

    public static Calendar of(Collection<Event> events) {
        List<Range> ranges = events.stream()
                .map(Range::new)
                .toList();
        return new Calendar(ranges);
    }
}
