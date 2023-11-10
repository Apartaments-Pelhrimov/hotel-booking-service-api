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
import ua.mibal.booking.model.entity.Hotel;
import ua.mibal.booking.model.search.Request;

import java.util.Optional;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    @Query("select distinct h from Hotel h " +
           "left join fetch h.photos " +

           "left join h.options ho " +
           "left join h.apartments a " +
           "left join a.apartmentOptions ao " +
           "left join a.reservations r " +
           "where " +

           "(lower(h.name) like lower(concat('%', :#{#r.query}, '%')) or lower(h.location.city) like lower(concat('%', :#{#r.query}, '%'))) and " +
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
    Page<Hotel> findAllByQuery(@Param("r") Request request, Pageable pageable);

    @Query("select h from Hotel h " +
           "left join fetch h.photos " +
           "where h.id = ?1")
    Optional<Hotel> findByIdFetchPhotos(Long id);
}
