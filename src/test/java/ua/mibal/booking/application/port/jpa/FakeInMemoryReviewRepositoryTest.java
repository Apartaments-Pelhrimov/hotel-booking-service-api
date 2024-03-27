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

package ua.mibal.booking.application.port.jpa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.Review;
import ua.mibal.test.annotation.UnitTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.data.domain.Pageable.unpaged;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class FakeInMemoryReviewRepositoryTest {

    private final FakeInMemoryReviewRepository repository = new FakeInMemoryReviewRepository();

    private Review review;
    private List<Review> result;
    private boolean exists;
    private Apartment apartment;

    @BeforeEach
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    void findAll() {
        givenSavedReview(1L);
        givenSavedReview(2L);
        givenSavedReview(3L);

        whenFindAll();

        thenResultShouldContainAllReviews(1L, 2L, 3L);
    }

    @Test
    void save() {
        givenReview(1L);

        whenSave();

        whenExistsById(1L);
        thenExistsIs(true);
    }

    @Test
    void existsById() {
        givenSavedReview(1L);

        whenExistsById(1L);

        thenExistsIs(true);
    }

    @Test
    void findByApartmentIdFetchUser() {
        givenApartment(1L);
        givenSavedReviewForApartment(1L);
        givenSavedReviewForApartment(2L);
        givenSavedReviewForApartment(3L);

        whenFindByApartmentIdFetchUser(1L);

        thenResultShouldContainAllReviews(1L, 2L, 3L);
    }

    @Test
    void deleteById() {
        givenSavedReview(1L);

        whenDeleteById(1L);

        whenExistsById(1L);
        thenExistsIs(false);
    }

    @Test
    void deleteByIdShouldNotDeleteAll() {
        givenSavedReview(2L);

        whenDeleteById(101010L);

        whenExistsById(2L);
        thenExistsIs(true);
    }

    @Test
    void deleteAll() {
        givenReview(1L);
        givenReview(2L);
        givenReview(3L);

        whenDeleteAll();

        whenExistsById(1L);
        thenExistsIs(false);
        whenExistsById(2L);
        thenExistsIs(false);
        whenExistsById(3L);
        thenExistsIs(false);
    }

    private void whenDeleteById(long id) {
        repository.deleteById(id);
    }

    private void givenReview(long id) {
        review = new Review();
        review.setId(id);
    }

    private void givenSavedReview(long id) {
        givenReview(id);
        whenSave();
    }

    private void givenApartment(long id) {
        apartment = new Apartment();
        apartment.setId(id);
    }

    private void givenSavedReviewForApartment(long id) {
        givenReview(id);
        review.setApartment(apartment);
        whenSave();
    }

    private void whenFindByApartmentIdFetchUser(long apartmentId) {
        result = repository.findByApartmentIdFetchUser(apartmentId, unpaged());
    }

    private void thenResultShouldContainAllReviews(Long... ids) {
        List<Long> resultIds = result.stream()
                .map(Review::getId)
                .toList();
        assertThat(resultIds).containsOnly(ids);
    }

    private void whenSave() {
        repository.save(review);
    }

    private void whenExistsById(long id) {
        exists = repository.existsById(id);
    }

    private void whenFindAll() {
        result = repository.findAll(unpaged()).toList();
    }

    private void whenDeleteAll() {
        repository.deleteAll();
    }

    private void thenExistsIs(boolean expected) {
        assertEquals(expected, exists);
    }
}
