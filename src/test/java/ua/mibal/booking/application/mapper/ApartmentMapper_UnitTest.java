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

package ua.mibal.booking.application.mapper;

import org.assertj.core.api.Assertions;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Spy;
import ua.mibal.booking.application.dto.ChangeApartmentForm;
import ua.mibal.booking.application.dto.CreateApartmentForm;
import ua.mibal.booking.application.dto.request.UpdateApartmentOptionsDto;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.Apartment.ApartmentClass;
import ua.mibal.booking.domain.ApartmentInstance;
import ua.mibal.booking.domain.ApartmentOptions;
import ua.mibal.booking.domain.Price;
import ua.mibal.booking.domain.Room;
import ua.mibal.test.annotation.UnitTest;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class ApartmentMapper_UnitTest {

    private ApartmentMapper mapper;

    @Mock
    private ApartmentInstanceMapper apartmentInstanceMapper;
    @Mock
    private RoomMapper roomMapper;
    @Mock
    private PriceMapper priceMapper;

    @Spy
    private List<Price> prices = new ArrayList<>();
    @Spy
    private List<Room> rooms = new ArrayList<>();
    @Mock
    private ApartmentInstance apartmentInstance;

    @BeforeEach
    public void setup() {
        mapper = new ApartmentMapperImpl(priceMapper, roomMapper, apartmentInstanceMapper);
    }

    @ParameterizedTest
    @InstancioSource
    void assemble(CreateApartmentForm source) {
        when(priceMapper.assemble(source.prices()))
                .thenReturn(prices);
        when(roomMapper.toEntities(source.rooms()))
                .thenReturn(rooms);

        source.instances()
                .forEach(inst -> when(apartmentInstanceMapper.assemble(inst))
                        .thenReturn(apartmentInstance));
        when(apartmentInstanceMapper.assemble(source.instances()))
                .thenReturn(List.of(apartmentInstance));

        Apartment actual = mapper.assemble(source);

        assertThat(actual.getName(), is(source.name()));
        assertThat(actual.getApartmentClass(), is(source.apartmentClass()));
        assertThat(actual.getOptions(), is(source.options()));
        assertThat(actual.getPrices(), is(prices));
        assertThat(actual.getRooms(), is(rooms));
        if (!source.instances().isEmpty()) {
            Assertions.assertThat(actual.getApartmentInstances()).containsOnly(apartmentInstance);
        }
    }

    @Test
    void assemble() {
        Apartment actual = mapper.assemble(null);

        assertThat(actual, nullValue());
    }

    @ParameterizedTest
    @MethodSource("ua.mibal.test.util.DataGenerator#testApartments")
    void change_Apartment(Apartment original, ChangeApartmentForm changes) {
        mapper.change(original, changes);

        String expectedName = changes.name() == null
                ? original.getName()
                : changes.name();
        ApartmentClass expectedClass = changes.apartmentClass() == null
                ? original.getApartmentClass()
                : changes.apartmentClass();

        assertThat(original.getName(), is(expectedName));
        assertThat(original.getApartmentClass(), is(expectedClass));
    }

    @ParameterizedTest
    @MethodSource("ua.mibal.test.util.DataGenerator#testApartmentOptions")
    void change_ApartmentOptions(ApartmentOptions original, UpdateApartmentOptionsDto changes) {
        mapper.change(original, changes);

        boolean expectedMealsIncluded = changes.mealsIncluded() == null
                ? original.isMealsIncluded()
                : changes.mealsIncluded();
        boolean expectedKitchen = changes.kitchen() == null
                ? original.isKitchen()
                : changes.kitchen();
        boolean expectedBathroom = changes.bathroom() == null
                ? original.isBathroom()
                : changes.bathroom();
        boolean expectedWifi = changes.wifi() == null
                ? original.isWifi()
                : changes.wifi();
        boolean expectedRefrigerator = changes.refrigerator() == null
                ? original.isRefrigerator()
                : changes.refrigerator();

        assertThat(original.isMealsIncluded(), is(expectedMealsIncluded));
        assertThat(original.isKitchen(), is(expectedKitchen));
        assertThat(original.isBathroom(), is(expectedBathroom));
        assertThat(original.isWifi(), is(expectedWifi));
        assertThat(original.isRefrigerator(), is(expectedRefrigerator));
    }
}
