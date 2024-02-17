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

package ua.mibal.booking.service.security.jwt.component;

import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import ua.mibal.booking.config.properties.JwtTokenProps;
import ua.mibal.test.annotation.UnitTest;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class JwtTokenBuilder_UnitTest {

    private JwtTokenBuilder tokenBuilder;

    @Mock
    private JwtTokenProps tokenProps;

    @BeforeEach
    void setup() {
        tokenBuilder = new JwtTokenBuilder(tokenProps);
    }

    @Test
    void buildBy() {
        UserDetails userDetails = new TestUserDetails();
        String issuer = "https://localhost:8080";
        int tokenValidForDays = 111;

        when(tokenProps.validForDays())
                .thenReturn(tokenValidForDays);
        when(tokenProps.issuer())
                .thenReturn(issuer);

        JwtClaimsSet token = tokenBuilder.buildBy(userDetails);

        assertEquals(issuer, token.getIssuer().toExternalForm());
        assertEquals(userDetails.getUsername(), token.getSubject());
        assertEquals(
                token.getClaims().get("scope"),
                getScopeFromAuthorities(userDetails.getAuthorities()));
        Instant expectedExpiresAt = token.getIssuedAt()
                .plus(tokenValidForDays, DAYS);
        assertEquals(expectedExpiresAt, token.getExpiresAt());
    }

    private String getScopeFromAuthorities(Collection<? extends GrantedAuthority> grantedAuthorities) {
        return grantedAuthorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
    }

    @Getter
    private static class TestUserDetails implements UserDetails {
        protected String username = "username228";
        protected GrantedAuthority authority = () -> "TEST_AUTHORITY";
        protected Collection<? extends GrantedAuthority> authorities =
                List.of(authority);

        @Override
        public String getPassword() {
            return null;
        }

        @Override
        public boolean isAccountNonExpired() {
            return false;
        }

        @Override
        public boolean isAccountNonLocked() {
            return false;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return false;
        }

        @Override
        public boolean isEnabled() {
            return false;
        }
    }
}
