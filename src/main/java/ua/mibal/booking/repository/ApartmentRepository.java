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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.mibal.booking.model.entity.Apartment;
import ua.mibal.booking.model.search.Request;

import java.time.LocalDate;
import java.util.Optional;

public interface ApartmentRepository extends JpaRepository<Apartment, Long> {

    @Query("select a from Apartment a " +
           "left join fetch a.hotel " +
           "left join fetch a.photos " +
           "where a.id = ?1")
    Optional<Apartment> findByIdFetchPhotosHotel(Long id);

    @Query("select count(a) = 1 from Apartment a " +
           "left join a.reservations r " +
           "where " +
           "a.id = ?1 and " +
           "(r = null or r.details.reservedTo < ?2 or r.details.reservedFrom > ?3)")
    Boolean isFreeForRangeById(Long id, LocalDate from, LocalDate to);

    @Query("select distinct a from Apartment a " +
           "left join fetch a.photos " +

           "left join a.options ao " +
           "left join a.hotel h " +
           "left join h.options ho " +
           "left join a.reservations r " +
           "where " +

           "h.id = :hotelId and " +
           "lower(a.name) like lower(concat('%', :#{#r.query}, '%')) and " +
           "(r = null or r.details.reservedTo < :#{#r.from} or r.details.reservedFrom > :#{#r.to}) and " +
           "a.size >= :#{#r.adult} and " +

           "h.stars >= :#{#r.stars} and " +
           "a.oneDayCost <= :#{#r.maxPrice} and " +

           "ao.mealsIncluded in(:#{#r.meals}, true) and " +
           "ao.kitchen in(:#{#r.kitchen}, true) and " +
           "ao.bathroom in(:#{#r.bathroom}, true) and " +
           "ao.wifi in(:#{#r.wifi}, true) and " +
           "ao.refrigerator in(:#{#r.refrigerator}, true) and " +
           "ho.pool in(:#{#r.pool}, true) and " +
           "ho.restaurant in(:#{#r.restaurant}, true) and " +
           "ho.parking in(:#{#r.parking}, true)")
    Page<Apartment> findAllInHotel(@Param("hotelId") Long hotelId, @Param("r") Request request, Pageable pageable);
}
