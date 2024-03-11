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

package ua.mibal.booking.application.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.mibal.booking.application.ApartmentInstanceService;
import ua.mibal.booking.application.UserService;
import ua.mibal.booking.application.model.ReservationForm;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.ApartmentInstance;
import ua.mibal.booking.domain.Price;
import ua.mibal.booking.domain.Reservation;
import ua.mibal.booking.domain.ReservationDetails;
import ua.mibal.booking.domain.User;

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
    private final PriceCalculator priceCalculator;

    public Reservation buildBy(ReservationForm form) {
        User user = userService.getOne(form.userEmail());
        ApartmentInstance apartmentInstance =
                apartmentInstanceService.getFreeOneFetchApartmentAndPrices(form);
        ReservationDetails details = toDetails(apartmentInstance.getApartment(), form);
        return Reservation.of(user, apartmentInstance, details);
    }

    private ReservationDetails toDetails(Apartment apartment, ReservationForm form) {
        Price oneNightPriceOption = apartment.getPriceFor(form.people());
        BigDecimal reservationPrice = priceCalculator
                .calculateReservationPrice(oneNightPriceOption.getAmount(), form);
        return ReservationDetails.of(form, oneNightPriceOption, reservationPrice);
    }
}
