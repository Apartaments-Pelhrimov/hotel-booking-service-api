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

package ua.mibal.booking.adapter.in.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.mibal.booking.adapter.in.web.mapper.ApartmentDtoMapper;
import ua.mibal.booking.adapter.in.web.model.ApartmentCardDto;
import ua.mibal.booking.adapter.in.web.model.ApartmentDto;
import ua.mibal.booking.adapter.in.web.security.annotation.ManagerAllowed;
import ua.mibal.booking.application.ApartmentService;
import ua.mibal.booking.application.model.ChangeApartmentForm;
import ua.mibal.booking.application.model.ChangeApartmentOptionsForm;
import ua.mibal.booking.application.model.CreateApartmentForm;
import ua.mibal.booking.domain.Apartment;

import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/apartments")
public class ApartmentController {
    private final ApartmentService apartmentService;
    private final ApartmentDtoMapper apartmentDtoMapper;

    @GetMapping("/{id}")
    public ApartmentDto getOne(@PathVariable Long id) {
        Apartment apartment = apartmentService.getOneFetchPhotosBeds(id);
        return apartmentDtoMapper.toDto(apartment);
    }

    @GetMapping
    public List<ApartmentCardDto> getAll() {
        List<Apartment> apartments = apartmentService.getAllFetchPhotosBeds();
        return apartmentDtoMapper.toCardDtos(apartments);
    }

    @ManagerAllowed
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody @Valid CreateApartmentForm form) {
        apartmentService.create(form);
    }

    @ManagerAllowed
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void change(@PathVariable Long id,
                       @RequestBody @Valid ChangeApartmentForm form) {
        apartmentService.change(id, form);
    }

    @ManagerAllowed
    @PatchMapping("/{id}/options")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeOptions(@PathVariable Long id,
                              @RequestBody @Valid ChangeApartmentOptionsForm form) {
        apartmentService.changeOptions(id, form);
    }

    @ManagerAllowed
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        apartmentService.delete(id);
    }
}
