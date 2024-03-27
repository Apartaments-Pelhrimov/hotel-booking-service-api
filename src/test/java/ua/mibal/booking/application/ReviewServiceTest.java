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

package ua.mibal.booking.application;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import ua.mibal.booking.application.exception.ApartmentNotFoundException;
import ua.mibal.booking.application.exception.ReviewNotFoundException;
import ua.mibal.booking.application.exception.UserHasNoAccessToReviewException;
import ua.mibal.booking.application.exception.UserHasNoAccessToReviewsException;
import ua.mibal.booking.application.mapper.ReviewMapper;
import ua.mibal.booking.application.mapper.ReviewMapperImpl;
import ua.mibal.booking.application.model.CreateReviewForm;
import ua.mibal.booking.application.port.jpa.FakeInMemoryApartmentRepository;
import ua.mibal.booking.application.port.jpa.FakeInMemoryReviewRepository;
import ua.mibal.booking.application.port.jpa.FakeInMemoryUserRepository;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.ApartmentInstance;
import ua.mibal.booking.domain.Reservation;
import ua.mibal.booking.domain.Review;
import ua.mibal.booking.domain.User;
import ua.mibal.test.annotation.UnitTest;

import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.springframework.data.domain.Pageable.unpaged;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class ReviewServiceTest {

    private final FakeInMemoryReviewRepository reviewRepository = new FakeInMemoryReviewRepository();
    private final ReviewMapper reviewMapper = new ReviewMapperImpl();
    private final FakeInMemoryUserRepository userRepository = new FakeInMemoryUserRepository();
    private final FakeInMemoryApartmentRepository apartmentRepository = new FakeInMemoryApartmentRepository();

    private final ReviewService service = new ReviewService(reviewRepository, reviewMapper, userRepository, apartmentRepository);

    private Apartment apartment;
    private User user;
    private CreateReviewForm form;
    private Review review;

    @Test
    void create() {
        givenSavedApartment(101L);
        givenSavedUser("user@email.com");
        givenEmptyReviewRepository();
        givenReservationOfUserWithApartment();
        givenCreateReviewForm("Great apartment", 5.0, 101L, "user@email.com");

        whenCreate();

        thenReviewSaved("Great apartment", 5.0, 101L, "user@email.com");
    }

    @Test
    void createShouldThrowApartmentNotFoundException() {
        givenEmptyApartmentRepository();
        givenCreateReviewForm("Great apartment", 5.0, 101L, "user@email.com");

        assertThrows(ApartmentNotFoundException.class,
                this::whenCreate);
    }

    @Test
    void createShouldThrowUserHasNoAccessToReviewExceptionIfUserNotFound() {
        givenSavedApartment(101L);
        givenCreateReviewForm("Great apartment", 5.0, 101L, "user@email.com");

        assertThrows(UserHasNoAccessToReviewsException.class,
                this::whenCreate);
    }

    @Test
    void createShouldThrowUserHasNoAccessToReviewsExceptionIfUserHasNoReservationsWithApartment() {
        givenSavedApartment(101L);
        givenSavedUser("user@email.com");
        givenCreateReviewForm("Great apartment", 5.0, 101L, "user@email.com");

        assertThrows(UserHasNoAccessToReviewsException.class,
                this::whenCreate);
    }

    @Test
    void delete() {
        givenSavedUser("user@email");
        givenSavedApartment(101L);
        givenSavedReviewWithUserAndApartment(1L);

        whenDelete(1L, "user@email");

        thenReviewDoesNotExists(1L);
    }

    @Test
    void deleteShouldThrowUserHasNoAccessToReviewExceptionIfUserNotFound() {
        givenSavedUser("user@email");
        givenSavedApartment(101L);
        givenSavedReviewWithUserAndApartment(1L);

        assertThrows(UserHasNoAccessToReviewException.class,
                () -> whenDelete(1L, "anotherUser@email"));
    }

    @Test
    void deleteShouldThrowReviewNotFoundException() {
        givenSavedUser("user@email");
        givenSavedApartment(101L);
        givenSavedReviewWithUserAndApartment(1L);

        assertThrows(ReviewNotFoundException.class,
                () -> whenDelete(2L, "user@email"));
    }

    @Test
    void deleteShouldThrowUserHasNoAccessToReviewExceptionIfUserIsNotTheAuthorOfReview() {
        givenSavedUser("user@email");
        givenSavedUser("reviewAuthor@email");
        givenSavedApartment(101L);
        givenSavedReviewWithUserAndApartment(1L);

        assertThrows(UserHasNoAccessToReviewException.class,
                () -> whenDelete(1L, "user@email"));
    }

    private void thenReviewDoesNotExists(long id) {
        assertFalse(reviewRepository.existsById(id));
    }

    private void givenReview(long id) {
        review = new Review();
        review.setId(id);
    }

    private void givenSavedReviewWithUserAndApartment(long reviewId) {
        givenReview(reviewId);
        user.addReview(review);
        review.setApartment(apartment);
        reviewRepository.save(review);
    }

    private void whenDelete(long id, String userEmail) {
        service.delete(id, userEmail);
    }

    private void givenUser(String email) {
        user = new User();
        user.setEmail(email);
    }

    private void givenSavedUser(String email) {
        givenUser(email);
        userRepository.save(user);
    }

    private void givenApartment(long id) {
        apartment = new Apartment();
        apartment.setId(id);
    }

    private void givenSavedApartment(long id) {
        givenApartment(id);
        apartmentRepository.save(apartment);
    }

    private void givenReservationOfUserWithApartment() {
        ApartmentInstance apartmentInstance = new ApartmentInstance();
        apartmentInstance.setApartment(apartment);

        Reservation reservation = new Reservation();
        reservation.setApartmentInstance(apartmentInstance);

        user.addReservation(reservation);
    }

    private void thenReviewSaved(String body, double rate, long apartmentId, String username) {
        Page<Review> reviews = reviewRepository.findAll(unpaged());

        Predicate<Review> sameReviewCondition = r ->
                r.getBody().equals(body)
                && r.getRate() == rate
                && r.getApartment().getId() == apartmentId
                && r.getUser().getEmail().equals(username);

        assertThat(reviews).anyMatch(sameReviewCondition);
    }

    private void givenEmptyReviewRepository() {
        reviewRepository.deleteAll();
    }

    private void givenEmptyApartmentRepository() {
        apartmentRepository.deleteAll();
    }

    private void whenCreate() {
        service.create(form);
    }

    private void givenCreateReviewForm(String body, double rate, long apartmentId, String username) {
        form = new CreateReviewForm();
        form.setBody(body);
        form.setRate(rate);
        form.setApartmentId(apartmentId);
        form.setUserEmail(username);
    }
}
