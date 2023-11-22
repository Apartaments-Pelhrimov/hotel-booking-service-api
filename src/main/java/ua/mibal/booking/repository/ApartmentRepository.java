/*
 * Copyright (c) 2023. Mykhailo Balakhon, mailto:9mohapx9@gmail.com
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

package ua.mibal.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.mibal.booking.model.entity.Apartment;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ApartmentRepository extends JpaRepository<Apartment, Long> {

    @Query("""
            select a from Apartment a
                left join fetch a.hotel
                left join fetch a.photos
            where a.id = ?1
            """)
    Optional<Apartment> findByIdFetchPhotosHotel(Long id);

    @Query("""
            select (r = null or r.details.reservedTo < ?2 or r.details.reservedFrom > ?3)
            from Apartment a
                left join a.reservations r
            where a.id = ?1
            """)
    Optional<Boolean> isFreeForRangeById(Long id, LocalDate from, LocalDate to);

    @Query("""
            select a from Apartment a
                left join fetch a.photos
            """)
    List<Apartment> findAllFetchPhotos();

    @Query("""
            select a from Apartment a
                left join fetch a.photos
            where a.id = ?1
            """)
    Optional<Apartment> findByIdFetchPhotos(Long id);
}
