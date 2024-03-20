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

import ua.mibal.booking.domain.Token;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TokenRepository extends Repository<Token, Long> {

    Optional<Token> findNotExpiredForByValue(String tokenValue, LocalDateTime dateTime);

    int deleteExpiredFor(LocalDateTime dateTime);
}
