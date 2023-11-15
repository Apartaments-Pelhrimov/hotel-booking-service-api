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

package ua.mibal.booking.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.data.domain.Page;
import ua.mibal.booking.model.dto.response.HotelDto;
import ua.mibal.booking.model.dto.search.HotelSearchDto;
import ua.mibal.booking.model.entity.Hotel;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class HotelMapper {

    public abstract HotelSearchDto toSearchDto(Hotel hotel, BigDecimal minCost);

    public abstract HotelDto toDto(Hotel hotel);

    public Page<HotelSearchDto> toHotelSearchDtoPage(Page<Hotel> hotels, List<BigDecimal> costs) {
        if (hotels.getNumberOfElements() != costs.size()) throw new IllegalArgumentException();
        Iterator<BigDecimal> costIterator = costs.iterator();
        return hotels.map(hotel -> toSearchDto(hotel, costIterator.next()));
    }
}
