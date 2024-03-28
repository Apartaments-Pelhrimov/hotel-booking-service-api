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

import ua.mibal.booking.domain.Photo;
import ua.mibal.booking.domain.User;
import ua.mibal.booking.domain.id.ApartmentId;

import java.util.Optional;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public interface UserRepository extends Repository<User, Long> {

    User getReferenceByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<String> findPasswordByEmail(String email);

    boolean existsByEmail(String email);

    void deleteByEmail(String email);

    void updateUserPasswordByEmail(String newEncodedPassword, String email);

    void updateUserPhotoByEmail(Photo photo, String email);

    // TODO delete
    void deleteUserPhotoByEmail(String email);

    boolean userHasReservationWithApartment(String email, ApartmentId apartmentId);

    boolean userHasReview(String email, Long reviewId);

    int deleteNotEnabledWithNoTokens();
}
