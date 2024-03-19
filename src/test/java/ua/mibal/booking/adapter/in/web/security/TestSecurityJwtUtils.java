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

package ua.mibal.booking.adapter.in.web.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

import static java.util.Arrays.stream;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class TestSecurityJwtUtils {

    public static RequestPostProcessor jwt(String username, String... roles) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(grantedAuthoritiesFrom(roles))
                .jwt(builder -> builder
                        .claim("sub", username));
    }

    private static List<GrantedAuthority> grantedAuthoritiesFrom(String[] roles) {
        return stream(roles)
                .map(role -> (GrantedAuthority) () -> "ROLE_" + role)
                .toList();
    }
}
