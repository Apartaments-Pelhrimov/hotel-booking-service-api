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
import ua.mibal.booking.application.port.jpa.ApartmentInstanceRepository;
import ua.mibal.booking.domain.ApartmentInstance;
import ua.mibal.booking.domain.ReservationRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public interface ApartmentInstanceJpaRepository extends JpaRepository<ApartmentInstance, Long>, ApartmentInstanceRepository {

    @Query("""
            select ai
                from ApartmentInstance ai
                left join fetch ai.apartment a
                left join fetch a.prices
            where
                ai.apartment.id = ?1
                and (select (count(r.id) = 0)
                        from Reservation r
                        where r.apartmentInstance.id = ai.id
                            and not (r.state = 'REJECTED' or r.details.to < ?2 or r.details.from > ?3))
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
    List<ApartmentInstance> findFreeByRequestFetchApartmentAndPrices(Long id, LocalDateTime from, LocalDateTime to, int people);

    @Override
    default List<ApartmentInstance> findFreeByRequestFetchApartmentAndPrices(ReservationRequest request) {
        return findFreeByRequestFetchApartmentAndPrices(
                request.apartmentId(),
                request.from(),
                request.to(),
                request.people()
        );
    }

    @Override
    @Query("""
            select ai
                from ApartmentInstance ai
                left join fetch ai.reservations
            where ai.id = ?1
            """)
    Optional<ApartmentInstance> findByIdFetchReservations(Long id);

    @Override
    @Query("""
            select ai
                from ApartmentInstance ai
                left join fetch ai.reservations
            where ai.apartment.id = ?1
                order by ai.id
            """)
    List<ApartmentInstance> findByApartmentIdFetchReservations(Long apartmentId);
}
