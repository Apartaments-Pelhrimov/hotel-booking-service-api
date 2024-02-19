/*
 * Copyright (c) 2024. Mykhailo Balakhon mailto:9mohapx9@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ua.mibal.booking.application.component;

import org.springframework.stereotype.Component;
import ua.mibal.booking.model.exception.IllegalReservationDateRangeException;
import ua.mibal.booking.model.exception.service.PriceCalculatorException;
import ua.mibal.booking.model.request.ReservationRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

import static java.math.BigDecimal.ZERO;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Component
public class PriceCalculator {

    public BigDecimal calculateReservationPrice(BigDecimal oneNightPrice,
                                                ReservationRequest request) {
        validatePriceIsPositive(oneNightPrice);
        validateReservationDates(request);
        BigDecimal nights = calculateNights(request);
        return nights.multiply(oneNightPrice);
    }

    private BigDecimal calculateNights(ReservationRequest request) {
        LocalDate from = request.from().toLocalDate();
        LocalDate to = request.to().toLocalDate();
        int nights = Period.between(from, to).getDays();
        return BigDecimal.valueOf(nights);
    }

    private void validatePriceIsPositive(BigDecimal price) {
        if (price.compareTo(ZERO) < 0) {
            throw new PriceCalculatorException(
                    "Illegal one night price=" + price
            );
        }
    }

    private void validateReservationDates(ReservationRequest request) {
        if (!request.from().isBefore(request.to())) {
            throw new IllegalReservationDateRangeException();
        }
    }
}
