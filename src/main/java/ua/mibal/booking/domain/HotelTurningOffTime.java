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

package ua.mibal.booking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "hotel_turning_off_times")
public class HotelTurningOffTime implements Event {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, name = "\"from\"")
    private LocalDateTime from;

    @Column(nullable = false, name = "\"to\"")
    private LocalDateTime to;

    @Column
    private String event;

    // TODO rename

    @Override
    public LocalDateTime getStart() {
        return from;
    }

    @Override
    public LocalDateTime getEnd() {
        return to;
    }

    @Override
    public String getEventName() {
        return "Hotel turned off. Reason: " + Optional.ofNullable(event).orElse("");
    }
}
