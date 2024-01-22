/*
 * Copyright (c) 2023. Mykhailo Balakhon mailto:9mohapx9@gmail.com
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

package ua.mibal.booking.service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import ua.mibal.booking.model.entity.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class TokenService {
    private final JwtEncoder jwtEncoder;

    @Value(value = "${jwt-token.expiring-days}")
    private Integer tokenExpiresInDays = 10;

    public String generateJwtToken(User user) {
        return generateTokenFor(user.getEmail(), user.getRole().getGrantedAuthorities());
    }

    private String generateTokenFor(String username,
                                    Collection<? extends GrantedAuthority> grantedAuthorities) {
        JwtClaimsSet jwtClaims = jwtClaimsForUser(username, grantedAuthorities);
        Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(jwtClaims));
        return jwt.getTokenValue();
    }

    private JwtClaimsSet jwtClaimsForUser(String username,
                                          Collection<? extends GrantedAuthority> grantedAuthorities) {
        Instant now = Instant.now();
        String scope = getScopeFromAuthorities(grantedAuthorities);
        return JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(tokenExpiresInDays, ChronoUnit.DAYS))
                .subject(username)
                .claim("scope", scope)
                .build();
    }

    private String getScopeFromAuthorities(Collection<? extends GrantedAuthority> grantedAuthorities) {
        return grantedAuthorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
    }
}
