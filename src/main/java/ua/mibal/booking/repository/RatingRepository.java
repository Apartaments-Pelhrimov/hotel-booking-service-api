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

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@Repository
public class RatingRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void updateRatings() {
        entityManager.createQuery("""
                        update Apartment a
                            SET a.rating = (
                                select avg(c.rate) from Comment c
                                where c.apartment.id = a.id)
                        """)
                .executeUpdate();
        entityManager.createQuery("""
                        update Hotel h
                            SET h.rating = (
                                select avg(a.rating)
                                from Apartment a
                                where a.hotel.id = h.id and
                                a.rating != null)
                        """)
                .executeUpdate();
    }
}
