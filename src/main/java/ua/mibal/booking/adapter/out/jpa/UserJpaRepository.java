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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ua.mibal.booking.adapter.out.jpa.custom.CustomUserRepository;
import ua.mibal.booking.application.port.jpa.UserRepository;
import ua.mibal.booking.domain.Photo;
import ua.mibal.booking.domain.User;

import java.util.Optional;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public interface UserJpaRepository extends JpaRepository<User, Long>, CustomUserRepository, UserRepository {

    @Override
    Optional<User> findByEmail(String email);

    @Override
    @Query("""
            select u.password from User u
            where u.email = ?1
            """)
    Optional<String> findPasswordByEmail(String email);

    @Override
    boolean existsByEmail(String email);

    @Override
    void deleteByEmail(String email);

    @Override
    @Modifying
    @Query("""
            update User u
                set u.password = ?1
            where u.email = ?2
            """)
    void updateUserPasswordByEmail(String newEncodedPassword, String email);

    @Override
    @Modifying
    @Query("""
            update User u
                set u.photo = ?1
            where u.email = ?2
            """)
    void updateUserPhotoByEmail(Photo photo, String email);

    @Override
    @Modifying
    @Query("""
            update User u
                set u.photo = null
            where u.email = ?1
            """)
    void deleteUserPhotoByEmail(String email);

    @Override
    @Query("""
            select count(r.id) >= 1 from Reservation r
                left join r.user u
                left join r.apartmentInstance ai
                right join ai.apartment a
            where u.email = ?1 and a.id = ?2
            """)
    boolean userHasReservationWithApartment(String email, Long apartmentId);

    @Override
    @Query("""
            select count(c.id) = 1 from Comment c
                left join c.user u
            where u.email = ?1 and c.id = ?2
            """)
    boolean userHasComment(String email, Long commentId);

    @Override
    @Transactional
    @Modifying
    @Query("""
            delete from User u
            where
                u.enabled = false
            and (
                    select count(t.id)
                        from Token t
                    where t.user.id = u.id
            ) = 0
            """)
    int deleteNotEnabledWithNoTokens();
}
