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

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ua.mibal.booking.application.mapper.linker.ApartmentPhotoLinker;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.ApartmentOptions;
import ua.mibal.booking.model.dto.request.CreateApartmentDto;
import ua.mibal.booking.model.dto.request.UpdateApartmentDto;
import ua.mibal.booking.model.dto.request.UpdateApartmentOptionsDto;
import ua.mibal.booking.model.dto.response.ApartmentCardDto;
import ua.mibal.booking.model.dto.response.ApartmentDto;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Mapper(
        componentModel = SPRING,
        injectionStrategy = CONSTRUCTOR,
        uses = {
                ApartmentInstanceMapper.class,
                ApartmentPhotoLinker.class,
                RoomMapper.class,
                PriceMapper.class
        })
public interface ApartmentMapper {

    @Mapping(target = "price", source = "prices")
    @Mapping(target = "beds", source = "rooms")
    @Mapping(target = "photos", source = "apartment")
    ApartmentDto toDto(Apartment apartment);

    @Mapping(target = "price", source = "prices")
    @Mapping(target = "people", source = "rooms")
    @Mapping(target = "photos", source = "apartment")
    ApartmentCardDto toCardDto(Apartment apartment);

    @Mapping(target = "apartmentInstances", source = "instances")
    Apartment toEntity(CreateApartmentDto createApartmentDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Apartment apartment, UpdateApartmentDto updateApartmentDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget ApartmentOptions target, UpdateApartmentOptionsDto source);
}
