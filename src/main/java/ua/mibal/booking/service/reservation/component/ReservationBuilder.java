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

package ua.mibal.booking.service.reservation.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.mibal.booking.model.entity.Apartment;
import ua.mibal.booking.model.entity.ApartmentInstance;
import ua.mibal.booking.model.entity.Reservation;
import ua.mibal.booking.model.entity.User;
import ua.mibal.booking.model.entity.embeddable.Price;
import ua.mibal.booking.model.entity.embeddable.ReservationDetails;
import ua.mibal.booking.model.request.ReservationRequest;
import ua.mibal.booking.service.ApartmentInstanceService;
import ua.mibal.booking.service.UserService;

import java.math.BigDecimal;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Component
public class ReservationBuilder {
    private final ApartmentInstanceService apartmentInstanceService;
    private final UserService userService;
    private final CostCalculationService costCalculationService;

    public Reservation buildBy(ReservationRequest request) {
        User user = userService.getOne(request.userEmail());
        ApartmentInstance apartmentInstance =
                apartmentInstanceService.getFreeOneFetchApartmentAndPrices(request);
        ReservationDetails details = toDetails(apartmentInstance.getApartment(), request);
        return Reservation.of(user, apartmentInstance, details);
    }

    private ReservationDetails toDetails(Apartment apartment, ReservationRequest request) {
        Price oneNightPriceOption = apartment.getPriceForPeople(request.people());
        BigDecimal reservationPrice = costCalculationService
                .calculatePrice(oneNightPriceOption.getCost(), request);
        return ReservationDetails.of(request, reservationPrice, oneNightPriceOption);
    }
}