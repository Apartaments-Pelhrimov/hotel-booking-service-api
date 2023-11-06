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

package ua.mibal.booking.model.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.type.NumericBooleanConverter;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ApartmentOptions {

    public static final ApartmentOptions DEFAULT
            = new ApartmentOptions(false, false, false, false, false);

    @Convert(converter = NumericBooleanConverter.class)
    @Column(nullable = false)
    private Boolean mealsIncluded;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(nullable = false)
    private Boolean kitchen;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(nullable = false)
    private Boolean bathroom;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(nullable = false)
    private Boolean wifi;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(nullable = false)
    private Boolean refrigerator;
}
