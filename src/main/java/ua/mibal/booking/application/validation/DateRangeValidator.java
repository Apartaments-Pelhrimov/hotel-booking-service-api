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

package ua.mibal.booking.application.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ua.mibal.booking.application.validation.constraints.ValidDateRange;
import ua.mibal.booking.domain.DateRangeValidRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.Objects;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class DateRangeValidator implements
        ConstraintValidator<ValidDateRange, DateRangeValidRequest> {

    @Override
    public boolean isValid(DateRangeValidRequest request,
                           ConstraintValidatorContext context) {
        Comparable<Temporal> from = (Comparable<Temporal>) request.from();
        Temporal to = request.to();
        // now() < from < to
        Temporal now = getNowDependingOnRequest(request);
        return !Objects.isNull(from) && !Objects.isNull(to) &&
               from.compareTo(now) > 0 && from.compareTo(to) < 0;
    }

    private Temporal getNowDependingOnRequest(DateRangeValidRequest request) {
        Temporal example = request.from();
        if (example != null &&
            example.isSupported(ChronoField.HOUR_OF_DAY)) {
            return LocalDateTime.now();
        }
        return LocalDate.now();
    }
}
