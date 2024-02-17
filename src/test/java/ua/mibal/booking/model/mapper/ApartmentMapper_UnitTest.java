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

package ua.mibal.booking.model.mapper;

import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Spy;
import ua.mibal.booking.model.dto.request.CreateApartmentDto;
import ua.mibal.booking.model.dto.request.UpdateApartmentDto;
import ua.mibal.booking.model.dto.request.UpdateApartmentOptionsDto;
import ua.mibal.booking.model.dto.response.ApartmentCardDto;
import ua.mibal.booking.model.dto.response.ApartmentDto;
import ua.mibal.booking.model.entity.Apartment;
import ua.mibal.booking.model.entity.Apartment.ApartmentClass;
import ua.mibal.booking.model.entity.ApartmentInstance;
import ua.mibal.booking.model.entity.Room;
import ua.mibal.booking.model.entity.embeddable.ApartmentOptions;
import ua.mibal.booking.model.entity.embeddable.Bed;
import ua.mibal.booking.model.entity.embeddable.Price;
import ua.mibal.booking.model.mapper.linker.ApartmentPhotoLinker;
import ua.mibal.test.annotation.UnitTest;

import java.math.BigDecimal;
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
    private ApartmentPhotoLinker photoLinker;
    @Mock
    private RoomMapper roomMapper;
    @Mock
    private PriceMapper priceMapper;

    @Mock
    private List<String> photos;
    @Mock
    private List<Bed> beds;

    private Integer people = 1_000_003;
    @Mock
    private BigDecimal price;
    @Spy
    private List<Price> prices = new ArrayList<>();
    @Spy
    private List<Room> rooms = new ArrayList<>();
    @Spy
    private List<ApartmentInstance> apartmentInstances = new ArrayList<>();

    @BeforeEach
    public void setup() {
        mapper = new ApartmentMapperImpl(apartmentInstanceMapper, photoLinker, roomMapper, priceMapper);
    }

    @ParameterizedTest
    @InstancioSource
    void toDto(Apartment source) {
        when(photoLinker.toLinks(source))
                .thenReturn(photos);
        when(roomMapper.roomsToBeds(source.getRooms()))
                .thenReturn(beds);
        when(priceMapper.findMinPrice(source.getPrices()))
                .thenReturn(price);

        ApartmentDto actual = mapper.toDto(source);

        assertThat(actual.name(), is(source.getName()));
        assertThat(actual.photos(), is(photos));
        assertThat(actual.options(), is(source.getOptions()));
        assertThat(actual.beds(), is(beds));
        assertThat(actual.price(), is(price));
    }

    @Test
    void toDto_should_return_null() {
        ApartmentDto actual = mapper.toDto(null);

        assertThat(actual, nullValue());
    }

    @ParameterizedTest
    @InstancioSource
    void toCardDto(Apartment source) {
        when(photoLinker.toLinks(source))
                .thenReturn(photos);
        when(roomMapper.roomsToPeopleCount(source.getRooms()))
                .thenReturn(people);
        when(priceMapper.findMinPrice(source.getPrices()))
                .thenReturn(price);

        ApartmentCardDto actual = mapper.toCardDto(source);

        assertThat(actual.name(), is(source.getName()));
        assertThat(actual.photos(), is(photos));
        assertThat(actual.options(), is(source.getOptions()));
        assertThat(actual.rating(), is(source.getRating()));
        assertThat(actual.people(), is(people));
        assertThat(actual.price(), is(price));
    }

    @Test
    void toCardDto_should_return_null() {
        ApartmentCardDto actual = mapper.toCardDto(null);

        assertThat(actual, nullValue());
    }

    @ParameterizedTest
    @InstancioSource
    void toEntity(CreateApartmentDto source) {
        when(priceMapper.toEntities(source.prices()))
                .thenReturn(prices);
        when(roomMapper.toEntities(source.rooms()))
                .thenReturn(rooms);
        when(apartmentInstanceMapper.toEntities(source.instances()))
                .thenReturn(apartmentInstances);

        Apartment actual = mapper.toEntity(source);

        assertThat(actual.getName(), is(source.name()));
        assertThat(actual.getApartmentClass(), is(source.apartmentClass()));
        assertThat(actual.getOptions(), is(source.options()));
        assertThat(actual.getPrices(), is(prices));
        assertThat(actual.getRooms(), is(rooms));
        assertThat(actual.getApartmentInstances(), is(apartmentInstances));
    }

    @Test
    void toEntity() {
        Apartment actual = mapper.toEntity(null);

        assertThat(actual, nullValue());
    }

    @ParameterizedTest
    @MethodSource("ua.mibal.booking.testUtils.DataGenerator#testApartments")
    void update_Apartment(Apartment original, UpdateApartmentDto changes) {
        mapper.update(original, changes);

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
    @MethodSource("ua.mibal.booking.testUtils.DataGenerator#testApartmentOptions")
    void update_ApartmentOptions(ApartmentOptions original, UpdateApartmentOptionsDto changes) {
        mapper.update(original, changes);

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
