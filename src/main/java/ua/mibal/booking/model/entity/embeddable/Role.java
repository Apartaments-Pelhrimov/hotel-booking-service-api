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

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@Getter
public enum Role implements GrantedAuthority {

    ROLE_USER("ROLE_USER"),
    ROLE_MANAGER("ROLE_MANAGER", ROLE_USER);

    private final String authority;
    private final List<Role> children;

    Role(String authority, Role... children) {
        this.authority = authority;
        this.children = Arrays.stream(children).toList();
    }

    public Collection<? extends GrantedAuthority> getGrantedAuthorities() {
        List<Role> roles = new ArrayList<>(children);
        roles.add(this);
        return roles;
    }
}
