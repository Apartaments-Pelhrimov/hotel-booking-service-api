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
import ua.mibal.booking.application.dto.request.CreateApartmentInstanceDto;
import ua.mibal.booking.domain.ApartmentInstance;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Mapper(componentModel = SPRING)
public interface ApartmentInstanceMapper {

    ApartmentInstance toEntity(CreateApartmentInstanceDto createApartmentInstanceDto);

    List<ApartmentInstance> toEntities(List<CreateApartmentInstanceDto> createApartmentInstanceDtos);
}
