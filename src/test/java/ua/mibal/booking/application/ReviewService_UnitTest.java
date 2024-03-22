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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.springframework.data.domain.Pageable;
import ua.mibal.booking.application.exception.ApartmentNotFoundException;
import ua.mibal.booking.application.exception.ReviewNotFoundException;
import ua.mibal.booking.application.exception.UserHasNoAccessToReviewException;
import ua.mibal.booking.application.exception.UserHasNoAccessToReviewsException;
import ua.mibal.booking.application.mapper.ReviewMapper;
import ua.mibal.booking.application.model.CreateReviewForm;
import ua.mibal.booking.application.port.jpa.ApartmentRepository;
import ua.mibal.booking.application.port.jpa.ReviewRepository;
import ua.mibal.booking.application.port.jpa.UserRepository;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.Review;
import ua.mibal.booking.domain.User;
import ua.mibal.test.annotation.UnitTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class ReviewService_UnitTest {

    private ReviewService service;

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ReviewMapper reviewMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ApartmentRepository apartmentRepository;

    @Mock
    private Review review;
    @Mock
    private CreateReviewForm createReviewForm;
    @Mock
    private Apartment apartment;
    @Mock
    private User user;

    @BeforeEach
    void setup() {
        service = new ReviewService(reviewRepository, reviewMapper, userRepository, apartmentRepository);
    }

    @Test
    void getAllByApartment() {
        Long apartmentId = 1L;
        Pageable pageable = Pageable.ofSize(5);

        List<Review> reviewPage = List.of(review, review);

        when(reviewRepository.findByApartmentIdFetchUser(apartmentId, pageable))
                .thenReturn(reviewPage);

        var actual = service.getAllByApartment(apartmentId, pageable);

        assertThat(actual).containsOnly(review, review);
    }

    @Test
    void create() {
        String email = "email";
        Long apartmentId = 1L;

        when(createReviewForm.getApartmentId())
                .thenReturn(apartmentId);
        when(createReviewForm.getUserEmail())
                .thenReturn(email);

        when(apartmentRepository.existsById(apartmentId))
                .thenReturn(true);
        when(userRepository.userHasReservationWithApartment(email, apartmentId))
                .thenReturn(true);

        when(reviewMapper.assemble(createReviewForm))
                .thenReturn(review);
        when(apartmentRepository.getReferenceById(apartmentId))
                .thenReturn(apartment);
        when(userRepository.getReferenceByEmail(email))
                .thenReturn(user);

        service.create(createReviewForm);

        verify(review, times(1)).setApartment(apartment);
        verify(review, times(1)).setUser(user);
    }

    @Test
    void create_should_throw_ApartmentNotFoundException() {
        String email = "email";
        Long apartmentId = 1L;

        when(createReviewForm.getApartmentId())
                .thenReturn(apartmentId);
        when(createReviewForm.getUserEmail())
                .thenReturn(email);

        when(apartmentRepository.existsById(apartmentId))
                .thenReturn(false);

        assertThrows(ApartmentNotFoundException.class,
                () -> service.create(createReviewForm));

        verifyNoInteractions(review);
    }

    @Test
    void create_should_throw_UserHasNoAccessToReviewsException() {
        String email = "email";
        Long apartmentId = 1L;

        when(createReviewForm.getApartmentId())
                .thenReturn(apartmentId);
        when(createReviewForm.getUserEmail())
                .thenReturn(email);

        when(apartmentRepository.existsById(apartmentId))
                .thenReturn(true);
        when(userRepository.userHasReservationWithApartment(email, apartmentId))
                .thenReturn(false);

        assertThrows(UserHasNoAccessToReviewsException.class,
                () -> service.create(createReviewForm));

        verifyNoInteractions(review);
    }

    @ParameterizedTest
    @CsvSource({
            "1,          email1",
            "2,          email2",
            "1010101001, email3",
    })
    void delete(Long id, String email) {
        when(userRepository.userHasReview(email, id)).thenReturn(true);
        when(reviewRepository.existsById(id)).thenReturn(true);

        service.delete(id, email);

        verify(reviewRepository, times(1))
                .deleteById(id);

    }

    @ParameterizedTest
    @CsvSource({
            "1,          email1",
            "2,          email2",
            "1010101001, email3",
    })
    void delete_should_throw_NotFoundException(Long id, String email) {
        when(userRepository.userHasReview(email, id)).thenReturn(true);
        when(reviewRepository.existsById(id)).thenReturn(false);

        assertThrows(
                ReviewNotFoundException.class,
                () -> service.delete(id, email)
        );
        verify(reviewRepository, never()).deleteById(id);
    }

    @ParameterizedTest
    @CsvSource({
            "1,          email1",
            "2,          email2",
            "1010101001, email3",
    })
    void delete_should_throw_UserHasNoAccessToReview(Long id, String email) {
        when(userRepository.userHasReview(email, id)).thenReturn(false);
        when(reviewRepository.existsById(id)).thenReturn(true);

        assertThrows(
                UserHasNoAccessToReviewException.class,
                () -> service.delete(id, email)
        );
        verify(reviewRepository, never()).deleteById(id);
    }
}
