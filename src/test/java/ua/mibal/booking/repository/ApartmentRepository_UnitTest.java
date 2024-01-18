/*
 * Copyright (c) 2023. Mykhailo Balakhon mailto:9mohapx9@gmail.com
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

import org.hibernate.LazyInitializationException;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ua.mibal.booking.model.entity.Apartment;
import ua.mibal.booking.model.entity.Comment;

import java.util.ArrayList;
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
@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource(locations = "classpath:application.yaml")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ApartmentRepository_UnitTest {

    private final List<Apartment> savedEntities = new ArrayList<>();

    @Autowired
    private ApartmentRepository repo;

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private SessionFactory sessionFactory;
    private Statistics stats;

    @BeforeEach
    void statisticsSetUp() {
        stats = sessionFactory.getStatistics();
        stats.clear();
    }

    @Test
    void findById_should_throw_LazyInitializationException_when_call_lazy_collections() {
        Long id = persistTestApartment();

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
        Long id = persistTestApartment();

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
        Long id = persistTestApartment();

        Apartment managedApartment = repo.findByIdFetchPhotos(id)
                .orElseThrow();

        entityManager.detach(managedApartment);
        assertDoesNotThrow(() -> managedApartment.getPhotos().size());

        assertThat(stats.getEntityLoadCount()).isOne();
        assertThat(stats.getCollectionLoadCount()).isOne();
    }

    @Test
    void findByIdFetchPrices() {
        Long id = persistTestApartment();

        Apartment managedApartment = repo.findByIdFetchPrices(id)
                .orElseThrow();

        entityManager.detach(managedApartment);
        assertDoesNotThrow(() -> managedApartment.getPrices().size());

        assertThat(stats.getEntityLoadCount()).isOne();
        assertThat(stats.getCollectionLoadCount()).isOne();
    }

    @Test
    void findAllFetchPhotos() {
        persistTestApartment();
        persistTestApartment();

        List<Apartment> actual = repo.findAllFetchPhotos();

        actual.forEach(entityManager::detach);
        actual.forEach(ap -> assertDoesNotThrow(() -> ap.getPhotos().size()));

        assertThat(stats.getEntityLoadCount()).isEqualTo(actual.size());
        assertThat(stats.getCollectionLoadCount()).isEqualTo(actual.size());
    }

    @Test
    void Apartment_auto_rating_computing() {
        Long id = persistTestApartment();

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

    private Long persistTestApartment() {
        Apartment testApartment = testApartment();
        testApartment.setRating(null);
        entityManager.persistAndFlush(testApartment);
        entityManager.detach(testApartment);
        savedEntities.add(testApartment);
        return testApartment.getId();
    }
}