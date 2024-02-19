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

package ua.mibal.booking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.mibal.booking.model.request.ReservationRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class ReservationDetails {

    @Column(nullable = false, name = "\"from\"")
    private LocalDateTime from;

    @Column(nullable = false, name = "\"to\"")
    private LocalDateTime to;

    @Embedded
    private Price price;

    @Column(nullable = false)
    private BigDecimal fullPrice;

    public static ReservationDetails of(ReservationRequest request, Price oneNightPriceOption,  BigDecimal reservationPrice) {
        return new ReservationDetails(request.from(), request.to(), oneNightPriceOption, reservationPrice);
    }
}
