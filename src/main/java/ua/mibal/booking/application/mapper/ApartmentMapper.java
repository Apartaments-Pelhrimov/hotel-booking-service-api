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
import ua.mibal.booking.application.dto.request.CreateApartmentDto;
import ua.mibal.booking.application.dto.request.UpdateApartmentDto;
import ua.mibal.booking.application.dto.request.UpdateApartmentOptionsDto;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.ApartmentOptions;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Mapper(componentModel = SPRING,
        injectionStrategy = CONSTRUCTOR,
        uses = {
                PriceMapper.class,
                RoomMapper.class,
                ApartmentInstanceMapper.class
        })
public interface ApartmentMapper {

    @Mapping(target = "apartmentInstances", source = "instances")
    Apartment toEntity(CreateApartmentDto createApartmentDto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void update(@MappingTarget Apartment apartment, UpdateApartmentDto updateApartmentDto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void update(@MappingTarget ApartmentOptions target, UpdateApartmentOptionsDto source);
}
