package ua.mibal.booking.model.dto.response;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class Calendar extends ArrayList<List<Integer>> {

    public Calendar(List<List<Integer>> dayRange) {
        super(dayRange);
    }
}
