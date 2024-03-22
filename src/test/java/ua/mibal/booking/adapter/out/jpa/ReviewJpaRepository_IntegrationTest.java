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

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.Review;
import ua.mibal.booking.domain.User;
import ua.mibal.test.annotation.JpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@JpaTest
class ReviewJpaRepository_IntegrationTest {
    private static final LocalDateTime NOW = now();

    @Autowired
    private ReviewJpaRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;
    private Apartment apartment;
    private List<Review> reviews;
    private Pageable pageable;

    private List<Review> result;

    @Test
    void findLatestFetchUser_shouldReturnLatest() {
        givenReviewsOfApartmentOfUserWithCreatedAt(
                NOW, NOW.minusDays(1), NOW.minusDays(2)
        );
        givenPageWithSize(2);

        whenFindLatestFetchUser();

        thenShouldContainReviewsWithCreatedAt(
                NOW, NOW.minusDays(1)
        );
    }

    @Test
    void findLatestFetchUser_shouldFetchUser() {
        givenReviewsOfApartmentOfUserWithCreatedAt(NOW);
        givenPageWithSize(1);

        whenDetachEntitiesFromSession();
        whenFindLatestFetchUser();

        thenShouldFetchUser();
    }

    private void givenReviewsOfApartmentOfUserWithCreatedAt(LocalDateTime... createdAts) {
        givenUser();
        givenApartment();

        givenReviewsWithCreatedAt(createdAts);

        assignReviewsToApartmentAndUser();

        reviews.forEach(entityManager::persistAndFlush);
    }

    private void givenUser() {
        user = Instancio.of(User.class)
                .set(field(User::getId), null)
                .create();
        entityManager.persistAndFlush(user);
    }

    private void givenApartment() {
        apartment = Instancio.of(Apartment.class)
                .set(field(Apartment::getId), null)
                .create();
        entityManager.persistAndFlush(apartment);
    }

    private void givenReviewsWithCreatedAt(LocalDateTime[] createdAts) {
        reviews = stream(createdAts)
                .map(this::givenReviewCreatedAt)
                .toList();
    }

    private Review givenReviewCreatedAt(LocalDateTime createAt) {
        return Instancio.of(Review.class)
                .set(field(Review::getId), null)
                .set(field(Review::getCreatedAt), createAt)
                .create();
    }

    private void assignReviewsToApartmentAndUser() {
        reviews.forEach(c -> c.setUser(user));
        reviews.forEach(c -> c.setApartment(apartment));
    }

    private void whenDetachEntitiesFromSession() {
        entityManager.detach(user);
        entityManager.detach(apartment);
        reviews.forEach(entityManager::detach);
    }

    private void givenPageWithSize(int size) {
        pageable = Pageable.ofSize(size);
    }

    private void whenFindLatestFetchUser() {
        result = repository.findLatestFetchUser(pageable);
    }

    private void thenShouldContainReviewsWithCreatedAt(LocalDateTime... createdAts) {
        assertThat(
                result.stream().map(Review::getCreatedAt)
        ).containsOnly(createdAts);
    }

    private void thenShouldFetchUser() {
        assertDoesNotThrow(() -> reviews.get(0).getUser().getEmail());
    }
}
