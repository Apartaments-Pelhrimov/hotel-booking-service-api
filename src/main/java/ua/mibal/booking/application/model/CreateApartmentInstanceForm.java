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

package ua.mibal.booking.application.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.mibal.booking.application.validation.constraints.Link;
import ua.mibal.booking.domain.id.ApartmentId;

import java.util.Objects;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateApartmentInstanceForm {

    @Size(min = 3)
    @NotBlank
    private String name;

    @Link
    private String bookingICalUrl;

    private ApartmentId apartmentId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CreateApartmentInstanceForm that = (CreateApartmentInstanceForm) o;

        if (!Objects.equals(name, that.name)) {
            return false;
        }
        if (!Objects.equals(bookingICalUrl, that.bookingICalUrl)) {
            return false;
        }
        return Objects.equals(apartmentId, that.apartmentId);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (bookingICalUrl != null ? bookingICalUrl.hashCode() : 0);
        result = 31 * result + (apartmentId != null ? apartmentId.hashCode() : 0);
        return result;
    }
}
