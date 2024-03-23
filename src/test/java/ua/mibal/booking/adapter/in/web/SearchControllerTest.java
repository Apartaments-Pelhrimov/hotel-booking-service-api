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
import ua.mibal.booking.application.model.SearchQuery;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.Apartment.ApartmentClass;
import ua.mibal.booking.domain.Bed;
import ua.mibal.booking.domain.Price;
import ua.mibal.booking.domain.Room;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.math.BigDecimal.valueOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.mibal.booking.adapter.in.web.security.TestSecurityJwtUtils.jwt;
import static ua.mibal.booking.domain.Apartment.ApartmentClass.COMFORT;
import static ua.mibal.booking.domain.Bed.Type.BUNK;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@WebMvcTest(SearchController.class)
class SearchControllerTest extends ControllerTest {

    @MockBean
    private ApartmentService apartmentService;

    @Test
    void searchInApartments() throws Exception {
        givenApartment("Apartment", "photo_key", 10, 2, valueOf(100.0), COMFORT);

        mvc.perform(get("/api/apartments")
                        .with(jwt("user@email.com", "USER"))
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
                            "name": "Apartment",
                            "photos": [
                              "http://localhost/api/photos/photo_key"
                            ],
                            "rating": 10,
                            "people": 2,
                            "price": 100.0
                          }
                        ]
                        """
                ));

        verify(apartmentService).getByQueryFetchPhotosPricesRoomsBeds(new SearchQuery(
                LocalDate.of(2024, 1, 1),
                null,
                2,
                COMFORT
        ));
    }

    private void givenApartment(String name, String photoKey, double rating, int people, BigDecimal price, ApartmentClass type) {
        Apartment apartment = new Apartment();
        apartment.setName(name);
        apartment.setRating(rating);
        apartment.addPhoto(photoKey);
        apartment.setApartmentClass(type);

        apartment.putPrice(new Price(1, price));

        Room room = new Room();
        room.getBeds().add(new Bed(people, BUNK));
        apartment.getRooms().add(room);

        when(apartmentService.getByQueryFetchPhotosPricesRoomsBeds(any()))
                .thenReturn(List.of(apartment));
    }
}
