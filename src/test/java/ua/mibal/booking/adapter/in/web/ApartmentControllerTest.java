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

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ua.mibal.booking.application.ApartmentService;
import ua.mibal.booking.application.exception.ApartmentNotFoundException;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.Bed;
import ua.mibal.booking.domain.Price;
import ua.mibal.booking.domain.Room;
import ua.mibal.booking.domain.id.ApartmentId;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.stream;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.mibal.booking.domain.Bed.Type.BUNK;
import static ua.mibal.booking.domain.Room.Type.LIVING_ROOM;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@WebMvcTest(ApartmentController.class)
class ApartmentControllerTest extends ControllerTest {

    @MockBean
    private ApartmentService apartmentService;

    @Test
    void getPropositions() throws Exception {
        givenApartment(
                new ApartmentId("amazing-apartment-id"),
                List.of("photo_key123"),
                "Amazing apartment",
                4.9,
                List.of(
                        new TestRoom("Room 1", LIVING_ROOM, new Bed(1, BUNK)),
                        new TestRoom("Room 2", LIVING_ROOM, new Bed(1, BUNK))
                ),
                List.of(
                        new Price(100, BigDecimal.valueOf(100)),
                        new Price(1, BigDecimal.valueOf(100_000_000))
                )
        );

        mvc.perform(get("/api/apartments/propositions"))

                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                          {
                            "id": "amazing-apartment-id",
                            "photo": "http://localhost/api/photos/photo_key123",
                            "name": "Amazing apartment",
                            "rating": 4.9,
                            "people": 2,
                            "price": 100
                          }
                        ]
                        """));
    }


    @Test
    void getOne() throws Exception {
        givenNoApartment();

        mvc.perform(get("/api/apartments/{apartmentId}", "apartment-id"))

                .andExpect(status().isNotFound());
    }

    private void givenNoApartment() {
        when(apartmentService.getOneFetchPhotosPricesRoomsBeds(any()))
                .thenThrow(new ApartmentNotFoundException(new ApartmentId("stub-id")));
    }

    private void givenApartment(ApartmentId id,
                                List<String> photoKeys,
                                String name,
                                double rating,
                                List<Room> rooms,
                                List<Price> prices) {
        Apartment apartment = new Apartment();
        apartment.setId(id);
        photoKeys.forEach(apartment::addPhoto);
        apartment.setName(name);
        apartment.setRating(rating);
        apartment.getRooms().addAll(rooms);
        apartment.getPrices().addAll(prices);

        when(apartmentService.getPropositionsFetchPhotosPricesRoomsBeds())
                .thenReturn(List.of(apartment));
        when(apartmentService.getOneFetchPhotosPricesRoomsBeds(id))
                .thenReturn(apartment);
    }

    private static class TestRoom extends Room {

        public TestRoom(String name, Type type, Bed... beds) {
            super();
            setName(name);
            setType(type);
            getBeds().addAll(stream(beds).toList());
        }
    }
}
