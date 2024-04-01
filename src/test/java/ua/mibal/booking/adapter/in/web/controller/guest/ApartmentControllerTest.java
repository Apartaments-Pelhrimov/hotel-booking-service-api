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

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ua.mibal.booking.adapter.in.web.controller.ControllerTest;
import ua.mibal.booking.application.ApartmentService;
import ua.mibal.booking.application.model.SearchQuery;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.ApartmentOptions;
import ua.mibal.booking.domain.Bed;
import ua.mibal.booking.domain.Price;
import ua.mibal.booking.domain.Room;
import ua.mibal.booking.domain.id.ApartmentId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.stream;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.mibal.booking.domain.Apartment.ApartmentClass.COMFORT;
import static ua.mibal.booking.domain.Bed.Type.BUNK;
import static ua.mibal.booking.domain.Bed.Type.CONNECTED;
import static ua.mibal.booking.domain.Bed.Type.TRANSFORMER;
import static ua.mibal.booking.domain.Room.Type.BEDROOM;
import static ua.mibal.booking.domain.Room.Type.LIVING_ROOM;
import static ua.mibal.booking.domain.Room.Type.MEETING_ROOM;

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
                List.of("photo_key123", "photo_key124"),
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
                        """, true));
    }

    @Test
    void getOne() throws Exception {
        givenApartment(
                new ApartmentId("amazing-apartment-id"),
                List.of("photo_key1", "photo_key2"),
                "Amazing apartment",
                4.9,
                List.of(
                        new TestRoom("Room 1", LIVING_ROOM),
                        new TestRoom("Room 2", MEETING_ROOM),
                        new TestRoom("Room 3", BEDROOM, new Bed(2, CONNECTED)),
                        new TestRoom("Room 4", BEDROOM, new Bed(1, TRANSFORMER))
                ),
                List.of(
                        new Price(100, BigDecimal.valueOf(100)),
                        new Price(1, BigDecimal.valueOf(100_000_000))
                ),
                ApartmentOptions.builder()
                        .mealsIncluded(true)
                        .kitchen(false)
                        .bathroom(true)
                        .wifi(false)
                        .refrigerator(true)
                        .build()
        );

        mvc.perform(get("/api/apartments/{id}", "amazing-apartment-id"))

                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "id": "amazing-apartment-id",
                          "name": "Amazing apartment",
                          "photos": [
                            "http://localhost/api/photos/photo_key1",
                            "http://localhost/api/photos/photo_key2"
                          ],
                          "rating": 4.9,
                          "options": {
                            "mealsIncluded": true,
                            "kitchen": false,
                            "bathroom": true,
                            "wifi": false,
                            "refrigerator": true
                          },
                          "rooms": [
                            {
                              "name": "Room 1",
                              "type": "LIVING_ROOM",
                              "beds": []
                            },
                            {
                              "name": "Room 2",
                              "type": "MEETING_ROOM",
                              "beds": []
                            },
                            {
                              "name": "Room 3",
                              "type": "BEDROOM",
                              "beds": [
                                {
                                  "size": 2,
                                  "type": "CONNECTED"
                                }
                              ]
                            },
                            {
                              "name": "Room 4",
                              "type": "BEDROOM",
                              "beds": [
                                {
                                  "size": 1,
                                  "type": "TRANSFORMER"
                                }
                              ]
                            }
                          ],
                          "prices": [
                            {
                              "person": 100,
                              "amount": 100
                            },
                            {
                              "person": 1,
                              "amount": 100000000
                            }
                          ]
                        }
                        """, true));
    }

    @Test
    void search() throws Exception {
        givenApartment(
                new ApartmentId("amazing-apartment-id"),
                List.of("photo_key123", "photo_key124"),
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

        mvc.perform(get("/api/apartments")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "checkIn": "2024-01-01",
                                    "guests": 2,
                                    "type": "COMFORT"
                                }
                                """))
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
                        """, true));

        verify(apartmentService).getByQueryFetchPhotosPricesRoomsBeds(new SearchQuery(
                LocalDate.of(2024, 1, 1),
                null,
                2,
                COMFORT
        ));
    }

    private Apartment givenApartment(ApartmentId id,
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
        when(apartmentService.getByQueryFetchPhotosPricesRoomsBeds(any()))
                .thenReturn(List.of(apartment));
        return apartment;
    }

    private void givenApartment(ApartmentId id,
                                List<String> photoKeys,
                                String name,
                                double rating,
                                List<Room> rooms,
                                List<Price> prices,
                                ApartmentOptions options) {
        Apartment apartment = givenApartment(id, photoKeys, name, rating, rooms, prices);
        apartment.setOptions(options);
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
