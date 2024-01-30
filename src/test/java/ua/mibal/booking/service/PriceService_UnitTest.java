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

package ua.mibal.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ua.mibal.booking.model.dto.request.PriceDto;
import ua.mibal.booking.model.entity.Apartment;
import ua.mibal.booking.model.entity.embeddable.Price;
import ua.mibal.booking.model.exception.entity.PriceNotFoundException;
import ua.mibal.booking.model.mapper.PriceMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static ua.mibal.booking.testUtils.CustomAssertions.assertEqualsList;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PriceService_UnitTest {

    private PriceService service;

    @Mock
    private ApartmentService apartmentService;
    @Mock
    private PriceMapper priceMapper;

    @Mock
    private Apartment apartment;
    @Mock
    private Price price;
    @Mock
    private PriceDto priceDto;

    @BeforeEach
    void setup() {
        service = new PriceService(apartmentService, priceMapper);
    }

    @Test
    public void getAllByApartment() {
        Long id = 1L;
        when(apartmentService.getOneFetchPrices(id)).thenReturn(apartment);
        when(apartment.getPrices()).thenReturn(List.of(price));
        when(priceMapper.toDto(price)).thenReturn(priceDto);

        List<PriceDto> actual = service.getAllByApartment(id);

        assertEqualsList(List.of(priceDto), actual);
    }

    @Test
    public void delete() {
        Long id = 1L;
        Integer person = 2;
        when(apartmentService.getOneFetchPrices(id)).thenReturn(apartment);
        when(apartment.deletePrice(person)).thenReturn(true);

        assertDoesNotThrow(() -> service.delete(id, person));
    }

    @Test
    public void delete_should_throw_PriceNotFoundException() {
        Long id = 1L;
        Integer person = 2;
        when(apartmentService.getOneFetchPrices(id)).thenReturn(apartment);
        when(apartment.deletePrice(person)).thenReturn(false);

        assertThrows(
                PriceNotFoundException.class,
                () -> service.delete(id, person)
        );
    }

    @Test
    public void put_should_create_if_price_not_found() {
        int people = 2;

        Apartment apartment = new Apartment();
        Price price = new Price(people, BigDecimal.TEN);

        Long id = 1L;
        when(priceMapper.toEntity(priceDto)).thenReturn(price);
        when(apartmentService.getOneFetchPrices(id)).thenReturn(apartment);

        service.put(id, priceDto);

        assertEquals(
                price,
                apartment.getPriceFor(people)
        );
    }

    @Test
    public void put_should_change_price_if_price_exists() {
        int people = 2;

        Apartment apartment = new Apartment();
        Price addedPrice = new Price(people, BigDecimal.ONE);
        apartment.putPrice(addedPrice);

        Price newPrice = new Price(people, BigDecimal.ONE);

        Long id = 1L;
        when(priceMapper.toEntity(priceDto)).thenReturn(newPrice);
        when(apartmentService.getOneFetchPrices(id)).thenReturn(apartment);

        service.put(id, priceDto);

        assertEquals(
                newPrice,
                apartment.getPriceFor(people)
        );
    }
}
