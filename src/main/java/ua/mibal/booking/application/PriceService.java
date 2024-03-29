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

package ua.mibal.booking.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.mibal.booking.application.exception.PriceNotFoundException;
import ua.mibal.booking.application.mapper.PriceMapper;
import ua.mibal.booking.application.model.PutPriceForm;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.Price;
import ua.mibal.booking.domain.id.ApartmentId;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class PriceService {
    private final ApartmentService apartmentService;
    private final PriceMapper priceMapper;

    @Transactional
    public void put(PutPriceForm form) {
        Price price = priceMapper.assemble(form);
        Apartment apartment = apartmentService.getOneFetchPrices(form.getApartmentId());
        apartment.putPrice(price);
    }

    @Transactional
    public void delete(ApartmentId apartmentId, Integer person) {
        Apartment apartment = apartmentService.getOneFetchPrices(apartmentId);
        if (!apartment.deletePrice(person)) {
            throw new PriceNotFoundException(person);
        }
    }
}
