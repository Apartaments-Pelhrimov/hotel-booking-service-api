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
import org.springframework.data.domain.Pageable;
import ua.mibal.booking.domain.Photo;
import ua.mibal.booking.domain.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class FakeInMemoryUserRepository implements UserRepository {

    private final Map<String, User> users = new HashMap<>();

    @Override
    public User save(User user) {
        return users.put(user.getEmail(), user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return users.values().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    @Override
    public User getReferenceById(Long aLong) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteById(Long aLong) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean existsById(Long aLong) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(User entity) {
        users.remove(entity.getEmail());
    }

    @Override
    public User getReferenceByEmail(String email) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(users.get(email));
    }

    @Override
    public Optional<String> findPasswordByEmail(String email) {
        return Optional.ofNullable(users.get(email))
                .map(User::getPassword);
    }

    @Override
    public boolean existsByEmail(String email) {
        return users.containsKey(email);
    }

    @Override
    public void deleteByEmail(String email) {
        users.remove(email);
    }

    @Override
    public void updateUserPasswordByEmail(String newEncodedPassword, String email) {
        findByEmail(email)
                .ifPresent(user -> user
                        .setPassword(newEncodedPassword)
                );
    }

    @Override
    public void updateUserPhotoByEmail(Photo photo, String email) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteUserPhotoByEmail(String email) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean userHasReservationWithApartment(String email, Long apartmentId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean userHasReview(String email, Long reviewId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int deleteNotEnabledWithNoTokens() {
        int before = users.size();
        users.values().removeIf(user ->
                !user.isEnabled()
                && user.getToken() == null
        );
        int after = users.size();

        return before - after;
    }

    public void deleteAll() {
        users.clear();
    }
}
