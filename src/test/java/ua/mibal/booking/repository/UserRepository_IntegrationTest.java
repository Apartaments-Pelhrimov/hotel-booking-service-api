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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ua.mibal.booking.model.entity.Apartment;
import ua.mibal.booking.model.entity.ApartmentInstance;
import ua.mibal.booking.model.entity.Comment;
import ua.mibal.booking.model.entity.Reservation;
import ua.mibal.booking.model.entity.User;
import ua.mibal.booking.model.entity.embeddable.Photo;
import ua.mibal.test.annotation.JpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ua.mibal.booking.testUtils.DataGenerator.testApartment;
import static ua.mibal.booking.testUtils.DataGenerator.testApartmentInstance;
import static ua.mibal.booking.testUtils.DataGenerator.testComment;
import static ua.mibal.booking.testUtils.DataGenerator.testReservation;
import static ua.mibal.booking.testUtils.DataGenerator.testUser;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@JpaTest
class UserRepository_IntegrationTest {

    private static final User user = testUser();

    @Autowired
    private UserRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    public void beforeEach() {
        entityManager.merge(user);
    }

    @Test
    void findByEmail() {
        assertEquals(
                user,
                repository.findByEmail(user.getEmail())
                        .orElseThrow()
        );
    }

    @Test
    void findByEmail_empty() {
        assertEquals(
                Optional.empty(),
                repository.findByEmail("not_exists@mail.com")
        );
    }

    @Test
    void findPasswordByEmail() {
        assertEquals(
                user.getPassword(),
                repository.findPasswordByEmail(user.getEmail())
                        .orElseThrow()
        );
    }

    @Test
    void findPasswordByEmail_empty() {
        assertEquals(
                Optional.empty(),
                repository.findPasswordByEmail("not_exists@mail.com")
        );
    }

    @Test
    void existsByEmail_true() {
        assertTrue(
                repository.existsByEmail(user.getEmail())
        );
    }

    @Test
    void existsByEmail_false() {
        assertFalse(
                repository.existsByEmail("not_exists@mail.com")
        );
    }

    @Test
    void deleteByEmail() {
        assertTrue(
                repository.existsByEmail(user.getEmail())
        );

        repository.deleteByEmail(user.getEmail());

        assertFalse(
                repository.existsByEmail(user.getEmail())
        );
    }

    @Test
    void updateUserPasswordByEmail() {
        String newPass = "new_pass";

        repository.updateUserPasswordByEmail(newPass, user.getEmail());

        entityManager.clear();
        User mergedUser = entityManager.find(User.class, user.getId());
        assertEquals(newPass, mergedUser.getPassword());
    }

    @Test
    void updateUserPhotoByEmail() {
        Photo newPhoto = new Photo("new_link");

        repository.updateUserPhotoByEmail(newPhoto, user.getEmail());

        entityManager.clear();
        User mergedUser = entityManager.find(User.class, user.getId());
        assertEquals(Optional.of(newPhoto), mergedUser.getPhoto());

    }

    @Test
    void deleteUserPhotoByEmail() {
        repository.deleteUserPhotoByEmail(user.getEmail());

        entityManager.clear();
        User mergedUser = entityManager.find(User.class, user.getId());
        assertEquals(Optional.empty(), mergedUser.getPhoto());
    }

    @Test
    void userHasReservationWithApartment_true() {
        Apartment apartment = entityManager.persistAndFlush(testApartment());

        ApartmentInstance apartmentInstance = testApartmentInstance();
        apartmentInstance.setApartment(apartment);
        entityManager.persistAndFlush(apartmentInstance);

        Reservation reservation = testReservation();
        reservation.setUser(user);
        reservation.setApartmentInstance(apartmentInstance);
        entityManager.persistAndFlush(reservation);

        assertTrue(
                repository.userHasReservationWithApartment(user.getEmail(), apartment.getId())
        );
    }

    @Test
    void userHasReservationWithApartment_false() {
        assertFalse(
                repository.userHasReservationWithApartment(user.getEmail(), 0L)
        );
    }

    @Test
    void userHasComment_true() {
        Apartment apartment = entityManager.persistAndFlush(testApartment());
        Comment comment = testComment();
        comment.setApartment(apartment);
        comment.setUser(user);
        entityManager.persistAndFlush(comment);

        assertTrue(
                repository.userHasComment(user.getEmail(), comment.getId())
        );
    }

    @Test
    void userHasComment_false() {
        Apartment apartment = entityManager.persistAndFlush(testApartment());
        Comment comment = testComment();
        comment.setApartment(apartment);
        comment.setUser(user);
        entityManager.persistAndFlush(comment);

        assertFalse(
                repository.userHasComment("another_email", comment.getId())
        );
    }
}
