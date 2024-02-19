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

package ua.mibal.booking.application.dto.auth;

import jakarta.validation.constraints.NotNull;
import ua.mibal.booking.application.validation.constraints.Email;
import ua.mibal.booking.application.validation.constraints.Name;
import ua.mibal.booking.application.validation.constraints.Password;
import ua.mibal.booking.application.validation.constraints.Phone;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public record RegistrationDto(

        @NotNull
        @Name
        String firstName,

        @NotNull
        @Name
        String lastName,

        @NotNull
        @Phone
        String phone,

        @NotNull
        @Email
        String email,

        @NotNull
        @Password
        String password
) {
}
