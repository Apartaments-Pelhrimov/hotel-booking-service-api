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

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static java.time.Period.between;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Service
public class CostCalculationService {

    public BigDecimal calculateFullCost(BigDecimal price, LocalDate from, LocalDate to) {
        validate(price, from, to);
        long nights = between(from, to).getDays();
        return price.multiply(valueOf(nights));
    }

    private void validate(BigDecimal price, LocalDate from, LocalDate to) {
        if (price.compareTo(ZERO) < 0)
            throw new IllegalArgumentException(
                    "Illegal price=" + price);
        if (!from.isBefore(to))
            throw new IllegalArgumentException(
                    "Illegal date range: from=" + from + " to=" + to);
    }
}
