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

package ua.mibal.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ua.mibal.booking.model.dto.HotelDto;
import ua.mibal.booking.model.dto.HotelSearchDto;
import ua.mibal.booking.model.entity.Hotel;

import java.math.BigDecimal;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface HotelMapper {

    @Mapping(target = "minCost", source = "minimalAllDaysPrice")
    HotelSearchDto toDto(Hotel hotel, BigDecimal minimalAllDaysPrice, Float rating);

    @Mapping(target = "description", source = "hotel.info.description")
    @Mapping(target = "rules", source = "hotel.info.rules")
    @Mapping(target = "notice", source = "hotel.info.notice")
    HotelDto toDto(Hotel hotel, Float rating);
}
