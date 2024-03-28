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

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ua.mibal.booking.domain.id.ApartmentId;

import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserRepository userRepository;
    private final ApartmentRepository apartmentRepository;

    public List<Review> getAllByApartment(ApartmentId apartmentId, Pageable pageable) {
        return reviewRepository.findByApartmentIdFetchUser(apartmentId, pageable);
    }

    public List<Review> getAllLatest(Pageable pageable) {
        return reviewRepository.findLatestFetchUser(pageable);
    }

    @Transactional
    public void create(CreateReviewForm form) {
        validateApartmentExists(form);
        validateUserHasReservationWithThisApartment(form);
        Review newReview = assembleReviewBy(form);
        reviewRepository.save(newReview);
    }

    public void delete(Long id, String userEmail) {
        validateReviewExists(id);
        validateUserHasReview(userEmail, id);
        reviewRepository.deleteById(id);
    }

    private Review assembleReviewBy(CreateReviewForm form) {
        Review newReview = reviewMapper.assemble(form);
        Apartment apartmentRef = apartmentRepository.getReferenceById(form.getApartmentId());
        User userRef = userRepository.getReferenceByEmail(form.getUserEmail());
        newReview.setApartment(apartmentRef);
        newReview.setUser(userRef);
        return newReview;
    }

    private void validateReviewExists(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new ReviewNotFoundException(id);
        }
    }

    private void validateUserHasReservationWithThisApartment(CreateReviewForm form) {
        if (!userRepository.userHasReservationWithApartment(
                form.getUserEmail(), form.getApartmentId())) {
            throw new UserHasNoAccessToReviewsException();
        }
    }

    private void validateApartmentExists(CreateReviewForm form) {
        if (!apartmentRepository.existsById(form.getApartmentId())) {
            throw new ApartmentNotFoundException(form.getApartmentId());
        }
    }

    private void validateUserHasReview(String userEmail, Long reviewId) {
        if (!userRepository.userHasReview(userEmail, reviewId)) {
            throw new UserHasNoAccessToReviewException();
        }
    }
}
