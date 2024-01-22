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

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ua.mibal.booking.model.dto.request.UpdateApartmentDto;
import ua.mibal.booking.model.dto.response.ApartmentCardDto;
import ua.mibal.booking.model.dto.response.ApartmentDto;
import ua.mibal.booking.model.entity.Apartment;
import ua.mibal.booking.model.entity.Room;
import ua.mibal.booking.model.entity.embeddable.ApartmentOptions;
import ua.mibal.booking.model.entity.embeddable.Bed;
import ua.mibal.booking.model.entity.embeddable.Price;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static ua.mibal.booking.testUtils.CustomAssertions.assertEqualsList;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApartmentMapperImpl.class)
@TestPropertySource(locations = "classpath:application.yaml")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ApartmentMapper_UnitTest {

    @Autowired
    private ApartmentMapper apartmentMapper;

    @MockBean
    private PhotoMapper photoMapper;

    @Mock
    private Apartment apartment;
    @Mock
    private ApartmentOptions options;

    private static List<Bed> addRoomsGetBeds(Apartment apartment) {
        List.of(
                new Room(null, "test room", List.of(new Bed(1, Bed.BedType.BUNK)), Room.RoomType.BEDROOM, null),
                new Room(null, "test room", List.of(new Bed(2, Bed.BedType.TRANSFORMER)), Room.RoomType.BEDROOM, null)
        ).forEach(r -> apartment.getRooms().add(r));
        return List.of(new Bed(1, Bed.BedType.BUNK), new Bed(2, Bed.BedType.TRANSFORMER));
    }

    private static BigDecimal addPricesGetMin(Apartment apartment) {
        List.of(
                new Price(1, BigDecimal.ZERO, null),
                new Price(6, BigDecimal.TEN, null)
        ).forEach(apartment::putPrice);
        return BigDecimal.ZERO;
    }

    private static Integer addRoomsGetSeats(Apartment apartment) {
        return addRoomsGetBeds(apartment).stream()
                .mapToInt(Bed::getSize)
                .sum();
    }

    @Test
    void toDto_correct_maps_cost_and_beds() {
        Apartment apartment = new Apartment();
        BigDecimal expectedMinPrice = addPricesGetMin(apartment);
        List<Bed> expectedBedList = addRoomsGetBeds(apartment);

        ApartmentDto actual = apartmentMapper.toDto(apartment);

        assertEquals(expectedMinPrice, actual.cost());
        assertEqualsList(expectedBedList, actual.beds());
    }

    @Test
    void toCardDto_correct_maps_cost_and_beds() {
        Apartment apartment = new Apartment();
        BigDecimal expectedMinPrice = addPricesGetMin(apartment);
        Integer expectedSeatsCount = addRoomsGetSeats(apartment);

        ApartmentCardDto actual = apartmentMapper.toCardDto(apartment);

        assertEquals(expectedMinPrice, actual.cost());
        assertEquals(expectedSeatsCount, actual.people());
    }

    // TODO
    @Test
    void toEntity() {
    }

    @Test
    void toInstance() {
    }

    @ParameterizedTest
    @CsvSource(value = {
            "name, COMFORT, true, true, true , true, true , false",
            "name, COMFORT, true, true, true , true, true , false",
            "name, COMFORT, true, true, true , true, false, false",
            "name, COMFORT, true, true, false, null, null , false",
            "name, COMFORT, true, true, false, null, null , false",
            "name, null   , true, true, false, null, null , false",
            "null, COMFORT, true, true, false, null, null , false",
            "name, COMFORT, true, true, true , true, true , true",
            "name, COMFORT, true, true, true , true, true , true",
            "null, COMFORT, true, true, true , true, true , true",
    }, nullValues = "null")
    void update(String name,
                Apartment.ApartmentClass apartmentClass,
                Boolean mealsIncluded,
                Boolean kitchen,
                Boolean bathroom,
                Boolean wifi,
                Boolean refrigerator,
                boolean sourceApartmentOptionsIsNull) {

        ApartmentOptions apartmentOptions = sourceApartmentOptionsIsNull
                ? null
                : new ApartmentOptions(mealsIncluded, kitchen, bathroom, wifi, refrigerator);
        when(apartment.getOptions()).thenReturn(options);

        apartmentMapper.update(
                apartment,
                new UpdateApartmentDto(name, apartmentClass, apartmentOptions)
        );

        if (name == null) {
            verify(apartment, never()).setName(any());
        } else {
            verify(apartment, times(1)).setName(name);
        }
        if (apartmentClass == null) {
            verify(apartment, never()).setApartmentClass(any());
        } else {
            verify(apartment, times(1)).setApartmentClass(apartmentClass);
        }
        if (sourceApartmentOptionsIsNull) {
            verify(apartment, never()).setOptions(any());
            verify(apartment, never()).getOptions();
            verifyNoInteractions(options);
        } else {
            if (mealsIncluded == null) {
                verify(options, never()).setMealsIncluded(any());
            } else {
                verify(options, times(1)).setMealsIncluded(mealsIncluded);
            }
            if (kitchen == null) {
                verify(options, never()).setKitchen(any());
            } else {
                verify(options, times(1)).setKitchen(kitchen);
            }
            if (bathroom == null) {
                verify(options, never()).setBathroom(any());
            } else {
                verify(options, times(1)).setBathroom(bathroom);
            }
            if (wifi == null) {
                verify(options, never()).setWifi(any());
            } else {
                verify(options, times(1)).setWifi(wifi);
            }
            if (refrigerator == null) {
                verify(options, never()).setRefrigerator(any());
            } else {
                verify(options, times(1)).setRefrigerator(refrigerator);
            }
        }
    }
}
