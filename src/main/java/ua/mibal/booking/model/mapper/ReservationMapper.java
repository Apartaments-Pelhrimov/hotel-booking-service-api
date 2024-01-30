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

import lombok.RequiredArgsConstructor;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ua.mibal.booking.model.dto.response.ReservationDto;
import ua.mibal.booking.model.entity.Apartment;
import ua.mibal.booking.model.entity.Reservation;
import ua.mibal.booking.model.entity.embeddable.Price;
import ua.mibal.booking.model.entity.embeddable.ReservationDetails;
import ua.mibal.booking.model.request.ReservationRequest;
import ua.mibal.booking.service.reservation.component.CostCalculationService;

import java.math.BigDecimal;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Mapper(uses = PhotoMapper.class,
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class ReservationMapper {
    private final CostCalculationService costCalculationService;

    @Mapping(target = "date", source = "dateTime")
    public abstract ReservationDto toDto(Reservation reservation);

    public ReservationDetails toDetails(Apartment apartment,
                                        ReservationRequest request) {
        Price oneNightPriceOption = apartment.getPriceForPeople(request.people());
        BigDecimal reservationPrice = costCalculationService
                .calculatePrice(oneNightPriceOption.getCost(), request);
        return ReservationDetails.of(request, reservationPrice, oneNightPriceOption);
    }
}
