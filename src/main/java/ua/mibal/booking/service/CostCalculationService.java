/*
 * Copyright (c) 2023. Mykhailo Balakhon mailto:9mohapx9@gmail.com
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

package ua.mibal.booking.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.mibal.booking.model.exception.service.CostCalculationServiceException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

import static java.math.BigDecimal.ZERO;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Service
public class CostCalculationService {
    private final static Logger log = LoggerFactory.getLogger(CostCalculationService.class);

    public BigDecimal calculatePrice(BigDecimal oneNightPrice, LocalDate from, LocalDate to) {
        validate(oneNightPrice, from, to);
        BigDecimal nights = calculateNights(from, to);
        return oneNightPrice.multiply(nights);
    }

    private BigDecimal calculateNights(LocalDate from, LocalDate to) {
        int nights = Period.between(from, to).getDays();
        return BigDecimal.valueOf(nights);
    }

    private void validate(BigDecimal price, LocalDate from, LocalDate to) {
        if (price.compareTo(ZERO) < 0) {
            throwE(new CostCalculationServiceException(
                    "Illegal one night price=" + price
            ));
        }
        if (!from.isBefore(to)) {
            throwE(new CostCalculationServiceException(
                    "Illegal date range: from=%s to=%s".formatted(
                            from, to
                    )
            ));
        }
    }

    private void throwE(RuntimeException e) {
        log.error(e.getMessage(), e);
        throw e;
    }
}
