/*
 * Copyright (c) 2023. Mykhailo Balakhon, mailto:9mohapx9@gmail.com
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

package ua.mibal.booking.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ua.mibal.booking.model.search.DateRangeRequestInterface;

import java.time.LocalDate;

import static java.time.LocalDate.now;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
public class DateRangeValidator implements ConstraintValidator<ValidDateRange, DateRangeRequestInterface> {

    @Override
    public boolean isValid(DateRangeRequestInterface request, ConstraintValidatorContext context) {
        LocalDate from = request.getFrom();
        LocalDate to = request.getTo();
        return from.isBefore(to) && from.isAfter(now().minusDays(1));
    }
}