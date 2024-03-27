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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ua.mibal.booking.domain.Review;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class FakeInMemoryReviewRepository implements ReviewRepository {

    private final Map<Long, Review> reviews = new HashMap<>();

    @Override
    public Review save(Review review) {
        return reviews.put(review.getId(), review);
    }

    @Override
    public Optional<Review> findById(Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Review getReferenceById(Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteById(Long id) {
        reviews.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return reviews.containsKey(id);
    }

    @Override
    public Page<Review> findAll(Pageable pageable) {
        return new PageImpl<>(reviews.values().stream().toList());
    }

    @Override
    public void delete(Review review) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Review> findByApartmentIdFetchUser(Long apartmentId, Pageable pageable) {
        return reviews.values().stream()
                .filter(review -> review.getApartment().getId().equals(apartmentId))
                .toList();
    }

    @Override
    public List<Review> findLatestFetchUser(Pageable pageable) {
        throw new UnsupportedOperationException();
    }

    public void deleteAll() {
        reviews.clear();
    }
}
