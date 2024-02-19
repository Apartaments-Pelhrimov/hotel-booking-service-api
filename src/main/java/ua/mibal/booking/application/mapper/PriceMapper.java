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

package ua.mibal.booking.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ua.mibal.booking.application.dto.request.PriceDto;
import ua.mibal.booking.domain.Price;

import java.math.BigDecimal;
import java.util.List;

import static java.lang.Integer.MAX_VALUE;
import static java.math.BigDecimal.valueOf;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PriceMapper {

    Price toEntity(PriceDto priceDto);

    List<Price> toEntities(List<PriceDto> priceDtos);

    PriceDto toDto(Price price);

    default BigDecimal findMinPrice(List<Price> prices) {
        if (prices == null || prices.isEmpty()) {
            return null;
        }
        return prices.stream()
                .map(Price::getAmount)
                .reduce(valueOf(MAX_VALUE), BigDecimal::min);
    }
}
