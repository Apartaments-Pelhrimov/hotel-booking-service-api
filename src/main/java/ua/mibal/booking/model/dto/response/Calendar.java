package ua.mibal.booking.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ua.mibal.booking.model.entity.Event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ua.mibal.booking.model.dto.response.Calendar.Range;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Getter
@Setter
@AllArgsConstructor
public class Calendar extends ArrayList<Range> {
    private Calendar(List<Range> ranges) {
        super(ranges);
    }

    public static Calendar of(Collection<Event> events) {
        return new Calendar(rangesFromEvents(events));
    }

    private static List<Range> rangesFromEvents(Collection<Event> events) {
        return events.stream()
                .map(Range::of)
                .toList();
    }


    public static class Range extends ArrayList<Integer> {
        private Range(Collection<? extends Integer> c) {
            super(c);
        }

        private static Range of(Event event) {
            int start = event.getStart()
                    .getDayOfMonth();
            int end = event.getEnd()
                    .getDayOfMonth();
            return new Range(List.of(start, end));
        }
    }
}
