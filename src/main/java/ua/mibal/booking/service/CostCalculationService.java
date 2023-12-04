package ua.mibal.booking.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

import static java.math.BigDecimal.valueOf;
import static java.time.Period.between;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Service
public class CostCalculationService {

    public BigDecimal calculateFullPriceForDays(BigDecimal cost, LocalDate from, LocalDate to) {
        long days = between(from, to).getDays();
        return cost.multiply(valueOf(days));
    }
}
