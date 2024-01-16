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

package ua.mibal.booking.model.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.mibal.booking.model.request.ReservationRequest;
import ua.mibal.booking.model.request.ReservationRequestDto;
import ua.mibal.booking.service.util.DateTimeUtils;

import java.time.LocalDateTime;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Component
public class ReservationRequestMapper {
    private final DateTimeUtils dateTimeUtils;

    public ReservationRequest toReservationRequest(ReservationRequestDto reservationRequestDto, Long apartmentId) {
        LocalDateTime from = dateTimeUtils.reserveFrom(reservationRequestDto.from());
        LocalDateTime to = dateTimeUtils.reserveTo(reservationRequestDto.to());
        return new ReservationRequest(
                from,
                to,
                reservationRequestDto.people(),
                apartmentId
        );
    }
}
