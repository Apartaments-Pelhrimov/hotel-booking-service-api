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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ua.mibal.booking.application.exception.PriceNotFoundException;
import ua.mibal.booking.application.mapper.PriceMapper;
import ua.mibal.booking.application.model.PutPriceForm;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.Price;
import ua.mibal.booking.domain.id.ApartmentId;
import ua.mibal.test.annotation.UnitTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class PriceService_UnitTest {

    private PriceService service;

    @Mock
    private ApartmentService apartmentService;
    @Mock
    private PriceMapper priceMapper;

    @Mock
    private Apartment apartment;
    @Mock
    private PutPriceForm form;

    @BeforeEach
    void setup() {
        service = new PriceService(apartmentService, priceMapper);
    }

    @Test
    void delete() {
        ApartmentId id = new ApartmentId("1L");
        Integer person = 2;
        when(apartmentService.getOneFetchPrices(id)).thenReturn(apartment);
        when(apartment.deletePrice(person)).thenReturn(true);

        assertDoesNotThrow(() -> service.delete(id, person));
    }

    @Test
    void delete_should_throw_PriceNotFoundException() {
        ApartmentId id = new ApartmentId("1L");
        Integer person = 2;
        when(apartmentService.getOneFetchPrices(id)).thenReturn(apartment);
        when(apartment.deletePrice(person)).thenReturn(false);

        assertThrows(
                PriceNotFoundException.class,
                () -> service.delete(id, person)
        );
    }

    @Test
    void put_should_create_if_price_not_found() {
        int people = 2;

        Apartment apartment = new Apartment();
        Price price = new Price(people, BigDecimal.TEN);

        ApartmentId id = new ApartmentId("1L");
        when(form.getApartmentId()).thenReturn(id);
        when(priceMapper.assemble(form)).thenReturn(price);
        when(apartmentService.getOneFetchPrices(id)).thenReturn(apartment);

        service.put(form);

        assertEquals(
                price,
                apartment.getPriceFor(people)
        );
    }

    @Test
    void put_should_change_price_if_price_exists() {
        int people = 2;

        Apartment apartment = new Apartment();
        Price addedPrice = new Price(people, BigDecimal.ONE);
        apartment.putPrice(addedPrice);

        Price newPrice = new Price(people, BigDecimal.ONE);

        ApartmentId id = new ApartmentId("1L");
        when(form.getApartmentId()).thenReturn(id);
        when(priceMapper.assemble(form)).thenReturn(newPrice);
        when(apartmentService.getOneFetchPrices(id)).thenReturn(apartment);

        service.put(form);

        assertEquals(
                newPrice,
                apartment.getPriceFor(people)
        );
    }
}
