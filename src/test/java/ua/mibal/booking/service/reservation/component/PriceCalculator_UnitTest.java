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

package ua.mibal.booking.service.reservation.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ua.mibal.booking.model.exception.IllegalReservationDateRangeException;
import ua.mibal.booking.model.exception.service.PriceCalculatorException;
import ua.mibal.booking.model.request.ReservationRequest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PriceCalculator_UnitTest {

    private PriceCalculator service;

    @Mock
    private ReservationRequest request;

    @BeforeEach
    void setup() {
        service = new PriceCalculator();
    }

    @ParameterizedTest
    @MethodSource("ua.mibal.booking.testUtils.DataGenerator#correctPriceCalculation")
    void calculatePrice(BigDecimal oneNightPrice, LocalDate from, LocalDate to, BigDecimal expected) {
        when(request.from()).thenReturn(from.atStartOfDay());
        when(request.to()).thenReturn(to.atStartOfDay());

        var actual = service.calculateReservationPrice(oneNightPrice, request);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("ua.mibal.booking.testUtils.DataGenerator#incorrectPriceForCalculation")
    void calculatePrice_should_throw_PriceCalculatorException(BigDecimal oneNightPrice,
                                                                     LocalDate from,
                                                                     LocalDate to) {
        when(request.from()).thenReturn(from.atStartOfDay());
        when(request.to()).thenReturn(to.atStartOfDay());

        assertThrows(
                PriceCalculatorException.class,
                () -> service.calculateReservationPrice(oneNightPrice, request)
        );
    }

    @ParameterizedTest
    @MethodSource("ua.mibal.booking.testUtils.DataGenerator#incorrectDateRangeForCalculation")
    void calculatePrice_should_throw_IllegalReservationDateRangeException(BigDecimal oneNightPrice,
                                                                          LocalDate from,
                                                                          LocalDate to) {
        when(request.from()).thenReturn(from.atStartOfDay());
        when(request.to()).thenReturn(to.atStartOfDay());

        assertThrows(
                IllegalReservationDateRangeException.class,
                () -> service.calculateReservationPrice(oneNightPrice, request)
        );
    }
}
