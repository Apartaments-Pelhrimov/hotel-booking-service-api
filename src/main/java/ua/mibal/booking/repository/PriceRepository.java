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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.mibal.booking.model.search.Request;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@Repository
public class PriceRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<BigDecimal> findMinPricePerDayInHotelByRequest(List<Long> ids, Request request) {
        return entityManager.createQuery("""
                        select min(a.oneDayCost)
                        from Hotel h
                                 left join h.options ho
                                 left join h.apartments a
                                 left join a.options ao
                                 left join a.reservations r
                        where h.id in (:ids) and
                                                
                               (r = null or r.details.reservedTo < :from or r.details.reservedFrom > :to) and
                               a.size >= :adult and

                               ao.mealsIncluded in(:meals, true) and
                               ao.kitchen in(:kitchen, true) and
                               ao.bathroom in(:bathroom, true) and
                               ao.wifi in(:wifi, true) and
                               ao.refrigerator in(:refrigerator, true) and
                               ho.pool in(:pool, true) and
                               ho.restaurant in(:restaurant, true) and
                               ho.parking in(:parking, true)
                        group by h.id
                        order by h.id
                        """, BigDecimal.class)
                .setParameter("ids", ids)
                .setParameter("from", request.getFrom())
                .setParameter("to", request.getTo())
                .setParameter("adult", request.getAdult())
                .setParameter("meals", request.getMeals())
                .setParameter("kitchen", request.getKitchen())
                .setParameter("bathroom", request.getBathroom())
                .setParameter("wifi", request.getWifi())
                .setParameter("refrigerator", request.getRefrigerator())
                .setParameter("pool", request.getPool())
                .setParameter("restaurant", request.getRestaurant())
                .setParameter("parking", request.getParking())
                .getResultList();
    }

    @Transactional(readOnly = true)
    public List<BigDecimal> findMinPricePerDayInHotel(List<Long> ids) {
        return entityManager.createQuery("""
                        select min(a.oneDayCost)
                        from Hotel h
                                 left join h.apartments a
                        where h.id in (:ids)
                        group by h.id
                        order by h.id
                        """, BigDecimal.class)
                .setParameter("ids", ids)
                .getResultList();
    }
}
