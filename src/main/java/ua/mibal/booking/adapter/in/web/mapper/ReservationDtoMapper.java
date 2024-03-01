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

package ua.mibal.booking.adapter.in.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import ua.mibal.booking.adapter.in.web.mapper.linker.PhotoLinker;
import ua.mibal.booking.application.dto.response.ReservationDto;
import ua.mibal.booking.domain.Reservation;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Mapper(componentModel = SPRING,
        uses = PhotoLinker.class)
public interface ReservationDtoMapper {

    default Page<ReservationDto> toDtos(Page<Reservation> reservations) {
        return reservations
                .map(this::toDto);
    }

    @Mapping(target = "apartment.photos",
            source = "reservation.apartmentInstance.apartment.photos")
    ReservationDto toDto(Reservation reservation);
}
