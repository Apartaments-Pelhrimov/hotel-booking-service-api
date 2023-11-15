/*
 * Copyright (c) 2023. Mykhailo Balakhon, mailto:9mohapx9@gmail.com
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

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ua.mibal.booking.model.entity.Hotel;
import ua.mibal.booking.model.search.Request;
import ua.mibal.booking.repository.PriceRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import static java.math.BigDecimal.valueOf;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@RequiredArgsConstructor
@Service
public class CostCalculationService {
    private final PriceRepository priceRepository;

    private BigDecimal calculate(BigDecimal oneDayCost, LocalDate from, LocalDate to) {
        long days = Period.between(from, to.plusDays(1)).getDays();
        return oneDayCost.multiply(valueOf(days));
    }

    public List<BigDecimal> calculateMinInHotels(Page<Hotel> hotels) {
        System.out.println(hotels);
        List<Long> ids = hotels
                .map(Hotel::getId)
                .toList();
        return priceRepository.findMinPricePerDayInHotel(ids); // FIXME order of prices
    }

    public List<BigDecimal> calculateMinInHotelsByRequest(Page<Hotel> hotels, Request request) {
        List<Long> ids = hotels
                .map(Hotel::getId)
                .toList();
        return priceRepository.findMinPricePerDayInHotelByRequest(ids, request) // FIXME order of prices
                .stream()
                .map(cost -> calculate(cost, request.getFrom(), request.getTo()))
                .toList();
    }
}
