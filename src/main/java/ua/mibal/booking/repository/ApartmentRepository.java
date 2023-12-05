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
import ua.mibal.booking.model.entity.ApartmentInstance;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ApartmentRepository extends JpaRepository<Apartment, Long> {

    @Query("""
            select a from Apartment a
                left join fetch a.photos
            where a.id = ?1
            """)
    Optional<Apartment> findByIdFetchPhotos(Long id);

    @Query("""
            select a from Apartment a
                left join fetch a.photos
            """)
    List<Apartment> findAllFetchPhotos();

    @Query("""
            select a from Apartment a
                left join fetch a.prices
            where a.id = ?1
            """)
    Optional<Apartment> findByIdFetchPrices(Long id);

    @Query("""
            select ai
                from ApartmentInstance ai
            where
                ai.apartment.id = ?1
                and (select (count(r.id) = 0)
                        from Reservation r
                        where r.apartmentInstance.id = ai.id
                            and not (r.state = 'REJECTED' or r.details.reservedTo < ?2 or r.details.reservedFrom > ?3))
                and (select (count(distinct tot) = 0)
                        from ApartmentInstance ai
                            left join ai.turningOffTimes tot
                        where not (tot.to < ?2 or tot.from > ?3))
                and (select (count(htot.id) = 0)
                        from HotelTurningOffTime htot
                        where not (htot.to < ?2 or htot.from > ?3))
                and (select (count(a.id) > 0)
                        from Apartment a
                            left join a.prices p
                        where a.id = ?1
                            and p.person = ?4)
            """)
    List<ApartmentInstance> findFreeApartmentInstanceByApartmentIdAndDates(Long id, LocalDateTime from, LocalDateTime to, int people);

    // TODO add hotel turn off time
    @Query("""
            select ai from ApartmentInstance ai
                left join ai.apartment a
                left join fetch ai.reservations r
            where
                a.id = ?1 and
                (r = null or (r.details.reservedFrom < ?3 and r.details.reservedTo > ?2))
            """)
    List<ApartmentInstance> findByApartmentIdBetweenFetchReservations(Long id, LocalDateTime start, LocalDateTime end);

    // TODO add hotel turn off time
    @Query("""
            select ai from ApartmentInstance ai
                left join fetch ai.reservations r
            where
                ai.id = ?1 and
                (r = null or (r.details.reservedFrom < ?3 and r.details.reservedTo > ?2))
            """)
    Optional<ApartmentInstance> findByApartmentInstanceIdBetweenFetchReservations(Long apartmentInstanceId, LocalDateTime start, LocalDateTime end);

    @Query("""
            select ai from ApartmentInstance ai
                left join fetch ai.reservations r
            where ai.id = ?1
            """)
    Optional<ApartmentInstance> findByApartmentInstanceIdFetchReservations(Long apartmentInstanceId);
}
