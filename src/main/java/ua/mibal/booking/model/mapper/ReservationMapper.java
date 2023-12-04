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
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ua.mibal.booking.model.dto.response.Calendar;
import ua.mibal.booking.model.dto.response.ReservationDto;
import ua.mibal.booking.model.entity.ApartmentInstance;
import ua.mibal.booking.model.entity.Reservation;

import java.util.List;

import static java.util.List.of;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Mapper(uses = PhotoMapper.class,
        componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class ReservationMapper {

    @Mapping(target = "date", source = "dateTime")
    public abstract ReservationDto toDto(Reservation reservation);

    public List<Calendar> toCalendarList(List<ApartmentInstance> apartmentInstances) {
        return apartmentInstances.stream()
                .map(this::toCalendar)
                .toList();
    }

    private Calendar toCalendar(ApartmentInstance apartmentInstance) {
        List<Calendar.Range> ranges = apartmentInstance.getReservations().stream()
                .map(this::reservationToRange)
                .toList();
        return new Calendar(apartmentInstance.getId(), ranges);
    }

    private Calendar.Range reservationToRange(Reservation reservation) {
        int start = reservation.getDetails()
                .getReservedFrom()
                .getDayOfMonth();
        int end = reservation.getDetails()
                          .getReservedTo()
                          .getDayOfMonth() - 1;
        return new Calendar.Range(of(start, end));
    }
}
