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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.mibal.booking.model.dto.request.CreateApartmentInstanceDto;
import ua.mibal.booking.service.ApartmentInstanceService;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@RestController
@RolesAllowed("MANAGER")
@RequestMapping("/api/apartments")
public class ApartmentInstanceController {
    private final ApartmentInstanceService apartmentInstanceService;

    @PostMapping("/{apartmentId}/instances")
    @ResponseStatus(HttpStatus.CREATED)
    public void addInstance(@PathVariable Long apartmentId,
                            @RequestBody @Valid CreateApartmentInstanceDto createApartmentInstanceDto) {
        apartmentInstanceService.createForApartment(apartmentId, createApartmentInstanceDto);
    }

    @DeleteMapping("/instances/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInstance(@PathVariable Long id) {
        apartmentInstanceService.delete(id);
    }
}
