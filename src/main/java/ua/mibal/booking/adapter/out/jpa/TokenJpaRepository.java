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
import ua.mibal.booking.application.port.jpa.TokenRepository;
import ua.mibal.booking.domain.Token;

import java.util.Optional;

public interface TokenJpaRepository extends JpaRepository<Token, Long>, TokenRepository {

    @Query("""
            select t
                from Token t
            where
                t.value = ?1
            and
                t.expiresAt > now()
            """)
    Optional<Token> findNotExpiredByValue(String tokenValue);

    @Transactional
    @Modifying
    @Query("""
            delete Token t
            where t.expiresAt < now()
            """)
    int deleteExpired();
}
