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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.mibal.booking.adapter.in.web.mapper.PriceDtoMapper;
import ua.mibal.booking.adapter.in.web.model.PriceDto;
import ua.mibal.booking.adapter.in.web.security.annotation.ManagerAllowed;
import ua.mibal.booking.application.PriceService;
import ua.mibal.booking.application.model.PutPriceForm;
import ua.mibal.booking.domain.Price;
import ua.mibal.booking.domain.id.ApartmentId;

import java.util.List;

import static org.springframework.http.HttpStatus.NO_CONTENT;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/apartments/{apartmentId}/prices")
public class PriceController {
    private final PriceService priceService;
    private final PriceDtoMapper priceDtoMapper;

    @GetMapping
    public List<PriceDto> getPrices(@PathVariable String apartmentId) {
        List<Price> allByApartment = priceService.getAllByApartment(new ApartmentId(apartmentId));
        return priceDtoMapper.toDtos(allByApartment);
    }

    @ManagerAllowed
    @PutMapping
    @ResponseStatus(NO_CONTENT)
    public void put(@PathVariable String apartmentId,
                    @RequestBody @Valid PutPriceForm form) {
        form.setApartmentId(new ApartmentId(apartmentId));
        priceService.put(form);
    }

    @ManagerAllowed
    @DeleteMapping
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable String apartmentId,
                       @RequestParam("person") Integer person) {
        priceService.delete(new ApartmentId(apartmentId), person);
    }
}
