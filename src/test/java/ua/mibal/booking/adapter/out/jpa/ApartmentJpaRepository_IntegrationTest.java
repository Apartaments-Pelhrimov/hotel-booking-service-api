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

import org.assertj.core.api.Assertions;
import org.hibernate.LazyInitializationException;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.Comment;
import ua.mibal.test.annotation.JpaTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ua.mibal.booking.testUtils.DataGenerator.testApartment;
import static ua.mibal.booking.testUtils.DataGenerator.testComment;
import static ua.mibal.booking.testUtils.DataGenerator.testUser;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@JpaTest
class ApartmentJpaRepository_IntegrationTest {

    @Autowired
    private ApartmentJpaRepository repo;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SessionFactory sessionFactory;
    private Statistics stats;

    private Apartment apartment;

    @BeforeEach
    void statisticsSetUp() {
        apartment = persistTestApartment();
        stats = sessionFactory.getStatistics();
        stats.clear();
    }

    @Test
    void findById_should_throw_LazyInitializationException_when_call_lazy_collections() {
        Long id = apartment.getId();

        Apartment managedApartment = repo.findById(id)
                .orElseThrow();

        entityManager.detach(managedApartment);
        assertThrows(LazyInitializationException.class,
                () -> managedApartment.getPhotos().size());
        assertThrows(LazyInitializationException.class,
                () -> managedApartment.getPrices().size());
        assertThrows(LazyInitializationException.class,
                () -> managedApartment.getApartmentInstances().size());
        assertThrows(LazyInitializationException.class,
                () -> managedApartment.getComments().size());
        assertThrows(LazyInitializationException.class,
                () -> managedApartment.getRooms().size());

        assertThat(stats.getEntityLoadCount()).isOne();
        assertThat(stats.getCollectionLoadCount()).isZero();
    }

    @Test
    void findById_should_not_throw_LazyInitializationException() {
        Long id = apartment.getId();

        Apartment managedApartment = repo.findById(id)
                .orElseThrow();

        assertDoesNotThrow(
                () -> managedApartment.getPhotos().size());
        assertDoesNotThrow(
                () -> managedApartment.getPrices().size());
        assertDoesNotThrow(
                () -> managedApartment.getApartmentInstances().size());
        assertDoesNotThrow(
                () -> managedApartment.getComments().size());
        assertDoesNotThrow(
                () -> managedApartment.getRooms().size());

        assertThat(stats.getEntityLoadCount()).isOne();
        assertThat(stats.getCollectionLoadCount()).isEqualTo(5);
    }

    @Test
    void findByIdFetchPhotos() {
        Long id = apartment.getId();

        Apartment managedApartment = repo.findByIdFetchPhotos(id)
                .orElseThrow();

        entityManager.detach(managedApartment);
        assertDoesNotThrow(() -> managedApartment.getPhotos().size());

        assertThat(stats.getEntityLoadCount()).isOne();
        assertThat(stats.getCollectionLoadCount()).isOne();

        Assertions.assertThat(stats.getQueryExecutionCount()).isOne();
        Assertions.assertThat(stats.getEntityFetchCount()).isZero();
        Assertions.assertThat(stats.getPrepareStatementCount())
                .isEqualTo(stats.getQueryExecutionCount() + stats.getEntityFetchCount());
    }

    @Test
    void findByIdFetchPrices() {
        Long id = apartment.getId();

        Apartment managedApartment = repo.findByIdFetchPrices(id)
                .orElseThrow();

        entityManager.detach(managedApartment);
        assertDoesNotThrow(() -> managedApartment.getPrices().size());

        assertThat(stats.getEntityLoadCount()).isOne();
        assertThat(stats.getCollectionLoadCount()).isOne();

        Assertions.assertThat(stats.getQueryExecutionCount()).isOne();
        Assertions.assertThat(stats.getEntityFetchCount()).isZero();
        Assertions.assertThat(stats.getPrepareStatementCount())
                .isEqualTo(stats.getQueryExecutionCount() + stats.getEntityFetchCount());
    }

    @Test
    void findAllFetchPhotos() {
        persistTestApartment();
        stats.clear();

        List<Apartment> actual = repo.findAllFetchPhotos();

        actual.forEach(entityManager::detach);
        actual.forEach(ap -> assertDoesNotThrow(() -> ap.getPhotos().size()));

        assertThat(stats.getEntityLoadCount()).isEqualTo(2);
        assertThat(stats.getCollectionLoadCount()).isEqualTo(2);

        Assertions.assertThat(stats.getQueryExecutionCount()).isOne();
        Assertions.assertThat(stats.getEntityFetchCount()).isZero();
        Assertions.assertThat(stats.getPrepareStatementCount())
                .isEqualTo(stats.getQueryExecutionCount() + stats.getEntityFetchCount());
    }

    @Test
    void Apartment_auto_rating_computing() {
        Long id = apartment.getId();

        Apartment apartmentWithoutRating =
                repo.findById(id).orElseThrow();
        Double emptyRating = apartmentWithoutRating.getRating();

        Comment newComment =
                persistTestCommentWithApartment(apartmentWithoutRating);
        entityManager.detach(apartmentWithoutRating);

        Double changedRating =
                repo.findById(id).orElseThrow()
                        .getRating();

        assertNotEquals(emptyRating, changedRating);
        assertEquals(newComment.getRate(), changedRating);
    }

    private Comment persistTestCommentWithApartment(Apartment apartment) {
        Comment comment = testComment();
        comment.setApartment(apartment);
        comment.setUser(entityManager.persistAndFlush(testUser()));
        return entityManager.persistAndFlush(comment);
    }

    private Apartment persistTestApartment() {
        Apartment testApartment = testApartment();
        testApartment.setRating(null);
        entityManager.persistAndFlush(testApartment);
        entityManager.detach(testApartment);
        return testApartment;
    }
}
