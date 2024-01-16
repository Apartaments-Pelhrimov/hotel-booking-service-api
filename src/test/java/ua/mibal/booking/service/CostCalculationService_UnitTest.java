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

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ua.mibal.booking.model.exception.IllegalReservationDateRangeException;
import ua.mibal.booking.model.exception.service.CostCalculationServiceException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CostCalculationService.class)
@TestPropertySource(locations = "classpath:application.yaml")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CostCalculationService_UnitTest {

    @Autowired
    private CostCalculationService service;

    @ParameterizedTest
    @MethodSource("ua.mibal.booking.testUtils.DataGenerator#correctPriceCalculation")
    void calculatePrice(BigDecimal oneNightPrice, LocalDate from, LocalDate to, BigDecimal expected) {
        var actual = service.calculatePrice(oneNightPrice, from, to);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("ua.mibal.booking.testUtils.DataGenerator#incorrectPriceForCalculation")
    void calculatePrice_should_throw_CostCalculationServiceException(BigDecimal oneNightPrice,
                                                                     LocalDate from,
                                                                     LocalDate to) {
        assertThrows(
                CostCalculationServiceException.class,
                () -> service.calculatePrice(oneNightPrice, from, to)
        );
    }

    @ParameterizedTest
    @MethodSource("ua.mibal.booking.testUtils.DataGenerator#incorrectDateRangeForCalculation")
    void calculatePrice_should_throw_IllegalReservationDateRangeException(BigDecimal oneNightPrice,
                                                                          LocalDate from,
                                                                          LocalDate to) {
        assertThrows(
                IllegalReservationDateRangeException.class,
                () -> service.calculatePrice(oneNightPrice, from, to)
        );
    }
}
