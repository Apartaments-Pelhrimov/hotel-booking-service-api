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

package ua.mibal.booking.adapter.in.web.controller.guest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.mibal.booking.adapter.in.web.controller.guest.docs.ApartmentControllerDocs;
import ua.mibal.booking.adapter.in.web.mapper.ApartmentDtoMapper;
import ua.mibal.booking.adapter.in.web.model.ApartmentCardDto;
import ua.mibal.booking.adapter.in.web.model.ApartmentDto;
import ua.mibal.booking.application.ApartmentService;
import ua.mibal.booking.application.model.SearchQuery;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.id.ApartmentId;

import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/apartments")
public class ApartmentController implements ApartmentControllerDocs {
    private final ApartmentService apartmentService;
    private final ApartmentDtoMapper apartmentDtoMapper;

    @Override
    @GetMapping("/propositions")
    public List<ApartmentCardDto> getPropositions() {
        List<Apartment> apartments = apartmentService.getPropositionsFetchPhotosPricesRoomsBeds();
        return apartmentDtoMapper.toCardDtos(apartments);
    }

    @Override
    @GetMapping("/{id}")
    public ApartmentDto getOne(@PathVariable String id) {
        Apartment apartment = apartmentService.getOneFetchPhotosPricesRoomsBeds(new ApartmentId(id));
        return apartmentDtoMapper.toDto(apartment);
    }

    @Override
    @GetMapping
    public List<ApartmentCardDto> search(@Valid @RequestBody SearchQuery searchQuery) {
        List<Apartment> apartments =
                apartmentService.getByQueryFetchPhotosPricesRoomsBeds(searchQuery);
        return apartmentDtoMapper.toCardDtos(apartments);
    }
}