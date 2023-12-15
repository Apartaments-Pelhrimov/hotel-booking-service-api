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
import ua.mibal.booking.model.entity.User;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
class UserRepository_UnitTest {
    private final static User user = testUser();

    @Autowired
    private UserRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    private void beforeEach() {
        entityManager.persistAndFlush(user);
    }

    @Test
    void findByEmail() {
    }

    @Test
    void findPasswordByEmail() {
    }

    @Test
    void existsByEmail() {
    }

    @Test
    void deleteByEmail() {
    }

    @Test
    void updateUserPasswordByEmail() {
    }

    @Test
    void updateUserPhotoByEmail() {
    }

    @Test
    void deleteUserPhotoByEmail() {
    }

    @Test
    void userHasReservationWithApartment() {
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
