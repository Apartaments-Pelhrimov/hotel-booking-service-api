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

package ua.mibal.booking.adapter.in.web.controller.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.mibal.booking.adapter.in.web.mapper.ReservationDtoMapper;
import ua.mibal.booking.adapter.in.web.model.ReservationDto;
import ua.mibal.booking.adapter.in.web.security.annotation.ManagerAllowed;
import ua.mibal.booking.application.ReservationService;
import ua.mibal.booking.domain.Reservation;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@ManagerAllowed
@RestController
@RequestMapping("/api")
public class ManagerReservationController {
    private final ReservationService reservationService;
    private final ReservationDtoMapper reservationDtoMapper;

    @GetMapping("/reservations")
    public Page<ReservationDto> getAll(Pageable pageable) {
        Page<Reservation> reservations = reservationService.getAll(pageable);
        return reservationDtoMapper.toDtos(reservations);
    }
}
