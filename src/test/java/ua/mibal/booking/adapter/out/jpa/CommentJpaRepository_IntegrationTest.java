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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.Comment;
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
class CommentJpaRepository_IntegrationTest {
    private static final LocalDateTime NOW = now();

    @Autowired
    private CommentJpaRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;
    private Apartment apartment;
    private List<Comment> comments;
    private Pageable pageable;

    private Page<Comment> result;

    @Test
    void findLatestFetchUser_shouldReturnLatest() {
        givenCommentsOfApartmentOfUserWithCreatedAt(
                NOW, NOW.minusDays(1), NOW.minusDays(2)
        );
        givenPageWithSize(2);

        whenFindLatestFetchUser();

        thenShouldContainCommentsWithCreatedAt(
                NOW, NOW.minusDays(1)
        );
    }

    @Test
    void findLatestFetchUser_shouldFetchUser() {
        givenCommentsOfApartmentOfUserWithCreatedAt(NOW);
        givenPageWithSize(1);

        whenDetachEntitiesFromSession();
        whenFindLatestFetchUser();

        thenShouldFetchUser();
    }

    private void givenCommentsOfApartmentOfUserWithCreatedAt(LocalDateTime... createdAts) {
        givenUser();
        givenApartment();

        givenCommentsWithCreatedAt(createdAts);

        assignCommentsToApartmentAndUser();

        comments.forEach(entityManager::persistAndFlush);
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

    private void givenCommentsWithCreatedAt(LocalDateTime[] createdAts) {
        comments = stream(createdAts)
                .map(this::givenCommentCreatedAt)
                .toList();
    }

    private Comment givenCommentCreatedAt(LocalDateTime createAt) {
        return Instancio.of(Comment.class)
                .set(field(Comment::getId), null)
                .set(field(Comment::getCreatedAt), createAt)
                .create();
    }

    private void assignCommentsToApartmentAndUser() {
        comments.forEach(c -> c.setUser(user));
        comments.forEach(c -> c.setApartment(apartment));
    }

    private void whenDetachEntitiesFromSession() {
        entityManager.detach(user);
        entityManager.detach(apartment);
        comments.forEach(entityManager::detach);
    }

    private void givenPageWithSize(int size) {
        pageable = Pageable.ofSize(size);
    }

    private void whenFindLatestFetchUser() {
        result = repository.findLatestFetchUser(pageable);
    }

    private void thenShouldContainCommentsWithCreatedAt(LocalDateTime... createdAts) {
        assertThat(
                result.stream().map(Comment::getCreatedAt)
        ).containsOnly(createdAts);
    }

    private void thenShouldFetchUser() {
        assertDoesNotThrow(() -> comments.get(0).getUser().getEmail());
    }
}
