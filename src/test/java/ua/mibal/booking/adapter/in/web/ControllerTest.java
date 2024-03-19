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

package ua.mibal.booking.adapter.in.web;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ua.mibal.booking.adapter.in.web.security.TestSecurityConfig;

import java.util.List;

import static java.util.Arrays.stream;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@ComponentScan(value = "ua.mibal.booking.adapter.in.web.mapper")
@Import(TestSecurityConfig.class)
public abstract class ControllerTest {

    protected MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    protected static RequestPostProcessor jwt(String username, String... roles) {
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

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }
}
