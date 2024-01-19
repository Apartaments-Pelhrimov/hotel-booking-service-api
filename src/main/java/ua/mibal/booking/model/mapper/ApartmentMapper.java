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

package ua.mibal.booking.model.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ua.mibal.booking.model.dto.request.CreateApartmentDto;
import ua.mibal.booking.model.dto.request.UpdateApartmentDto;
import ua.mibal.booking.model.dto.response.ApartmentCardDto;
import ua.mibal.booking.model.dto.response.ApartmentDto;
import ua.mibal.booking.model.entity.Apartment;
import ua.mibal.booking.model.entity.Room;
import ua.mibal.booking.model.entity.embeddable.ApartmentOptions;
import ua.mibal.booking.model.entity.embeddable.Bed;
import ua.mibal.booking.model.entity.embeddable.Price;

import java.math.BigDecimal;
import java.util.List;

import static java.lang.Integer.MAX_VALUE;
import static java.math.BigDecimal.valueOf;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Mapper(uses = PhotoMapper.class,
        componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class ApartmentMapper {

    @Mapping(target = "cost", source = "prices")
    @Mapping(target = "beds", source = "rooms")
    public abstract ApartmentDto toDto(Apartment apartment);

    @Mapping(target = "cost", source = "prices")
    @Mapping(target = "people", source = "rooms")
    public abstract ApartmentCardDto toCardDto(Apartment apartment);

    public abstract Apartment toEntity(CreateApartmentDto createApartmentDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void update(@MappingTarget Apartment apartment, UpdateApartmentDto updateApartmentDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void update(@MappingTarget ApartmentOptions target, ApartmentOptions source);

    protected List<Bed> roomsToBeds(List<Room> rooms) {
        return rooms.stream()
                .flatMap(room -> room.getBeds().stream())
                .toList();
    }

    protected Integer roomsToPeopleCount(List<Room> rooms) {
        return rooms.stream()
                .map(Room::getBeds)
                .mapToInt(beds -> beds.stream()
                        .mapToInt(Bed::getSize)
                        .sum()
                ).sum();
    }

    protected BigDecimal findMinPrice(List<Price> prices) {
        if (prices == null || prices.isEmpty()) {
            return null;
        }
        return prices.stream()
                .map(Price::getCost)
                .reduce(valueOf(MAX_VALUE), BigDecimal::min);
    }
}
