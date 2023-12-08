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

package ua.mibal.booking.model.dto.response.calendar;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.mibal.booking.model.entity.Event;

import java.time.LocalDateTime;
import java.time.Month;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Getter
@Setter
@NoArgsConstructor
public class Range {
    private DateEntry start;
    private DateEntry end;

    public Range(Event event) {
        start = DateEntry.of(event.getStart());
        end = DateEntry.of(event.getEnd());
    }

    public record DateEntry(int day, Month month) {

        public static DateEntry of(LocalDateTime date) {
            Month month = date.getMonth();
            int day = date.getDayOfMonth();
            return new DateEntry(day, month);
        }
    }
}
