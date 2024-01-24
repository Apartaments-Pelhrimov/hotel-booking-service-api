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

package ua.mibal.booking.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.mibal.booking.model.dto.request.TurnOffDto;
import ua.mibal.booking.service.TurningOffService;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@RolesAllowed("MANAGER")
@RestController
@RequestMapping("/api")
public class TurnOffController {
    private final TurningOffService turningOffService;

    @PatchMapping("/hotel/off")
    public void turnOffHotel(@Valid @RequestBody TurnOffDto turnOffDto) {
        turningOffService.turnOffHotel(turnOffDto);
    }

    @PatchMapping("/apartments/instances/{id}/off")
    public void turnOffApartmentInstance(@PathVariable Long id,
                                         @Valid @RequestBody TurnOffDto turnOffDto) {
        turningOffService.turnOffApartmentInstance(id, turnOffDto);
    }
}
