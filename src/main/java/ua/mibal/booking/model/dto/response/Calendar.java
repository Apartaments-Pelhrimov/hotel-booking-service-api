package ua.mibal.booking.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Getter
@Setter
@AllArgsConstructor
public class Calendar {
    private Long id;
    private List<Range> ranges;

    public static class Range extends ArrayList<Integer> {
        private Range(Collection<? extends Integer> c) {
            super(c);
        }

        public static Range of(int start, int end) {
            return new Range(List.of(start, end));
        }
    }
}
