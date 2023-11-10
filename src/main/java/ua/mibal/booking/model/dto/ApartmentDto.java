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

package ua.mibal.booking.model.dto;

import ua.mibal.booking.model.entity.embeddable.AdditionalInfo;
import ua.mibal.booking.model.entity.embeddable.ApartmentOptions;
import ua.mibal.booking.model.entity.embeddable.Bed;
import ua.mibal.booking.model.entity.embeddable.HotelOptions;
import ua.mibal.booking.model.entity.embeddable.Location;
import ua.mibal.booking.model.entity.embeddable.Photo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
public record ApartmentDto(
        String name,
        Set<Photo> photos,
        ApartmentOptions options,
        ApartmentHotelDto hotel,
        Float rating,
        List<Bed> beds,
        BigDecimal oneDayCost
) {
    public record ApartmentHotelDto(
            String name,
            HotelOptions options,
            Location location,
            AdditionalInfo info,
            Integer stars
    ) {
    }
}