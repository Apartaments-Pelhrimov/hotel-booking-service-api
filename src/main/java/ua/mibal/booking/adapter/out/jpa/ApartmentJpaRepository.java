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

package ua.mibal.booking.adapter.out.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ua.mibal.booking.application.port.jpa.ApartmentRepository;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.id.ApartmentId;

import java.util.List;
import java.util.Optional;

public interface ApartmentJpaRepository extends JpaRepository<Apartment, Long>, ApartmentRepository {

    @Override
    @Query("""
            select a from Apartment a
                left join fetch a.photos
            where a.id = ?1
            """)
    Optional<Apartment> findByIdFetchPhotos(ApartmentId id);

    @Override
    @Query("""
            select a from Apartment a
                left join fetch a.photos
            """)
    List<Apartment> findAllFetchPhotos();

    @Override
    @Query("""
            select a from Apartment a
                left join fetch a.prices
            where a.id = ?1
            """)
    Optional<Apartment> findByIdFetchPrices(ApartmentId id);

    @Override
    @Query("""
            select a from Apartment a
                left join fetch a.apartmentInstances
            where a.id = ?1
            """)
    Optional<Apartment> findByIdFetchInstances(ApartmentId id);

    @Override
    @Transactional(readOnly = true)
    default Optional<Apartment> findByIdFetchPhotosPricesRoomsBeds(ApartmentId id) {
        Optional<Apartment> apartment = findByIdFetchPhotos(id);
        apartment.ifPresent(a -> a.getPrices().size()); // to load prices
        apartment.stream()
                .flatMap(a -> a.getRooms().stream())    // to load rooms
                .forEach(r -> r.getBeds().size());      // to load beds
        return apartment;
    }

    @Override
    @Transactional(readOnly = true)
    default List<Apartment> findAllFetchFetchPhotosPricesRoomsBeds() {
        List<Apartment> apartments = findAllFetchPhotos();
        apartments.forEach(a -> a.getPrices().size());  // to load prices
        apartments.stream()
                .flatMap(a -> a.getRooms().stream())    // to load rooms
                .forEach(r -> r.getBeds().size());      // to load beds
        return apartments;
    }
}
