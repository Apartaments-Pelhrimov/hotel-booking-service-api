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

package ua.mibal.booking.model.search;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@Data
public class Request {
    private String query = "";
    @NotNull
    private LocalDate from;
    @NotNull
    private LocalDate to;
    @NotNull
    private Integer adult;
    @NotNull
    private Integer child;

    private Integer stars = 0;
    private Integer maxPrice = Integer.MAX_VALUE;
    private Float rating = 0f;

    private Boolean meals = false;
    private Boolean kitchen = false;
    private Boolean bathroom = false;
    private Boolean wifi = false;
    private Boolean refrigerator = false;
    private Boolean pool = false;
    private Boolean restaurant = false;
    private Boolean parking = false;
}
