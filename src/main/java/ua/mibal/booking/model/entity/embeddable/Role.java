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

package ua.mibal.booking.model.entity.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Embeddable
public class Role implements GrantedAuthority {

    public static final Role USER = new Role("ROLE_USER");
    public static final Role LOCAL_MANAGER = new Role("ROLE_LOCAL_MANAGER");
    public static final Role GLOBAL_MANAGER = new Role("ROLE_GLOBAL_MANAGER");

    @Column(nullable = false)
    private String authority;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return authority != null && authority.equals(role.authority);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
