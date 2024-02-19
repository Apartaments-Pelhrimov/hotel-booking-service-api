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

package ua.mibal.booking.application.security.jwt;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import ua.mibal.booking.application.security.jwt.JwtTokenService;
import ua.mibal.booking.application.security.jwt.component.JwtTokenBuilder;
import ua.mibal.test.annotation.UnitTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class JwtTokenService_UnitTest {

    private JwtTokenService service;

    @Mock
    private JwtEncoder encoder;
    @Mock
    private JwtTokenBuilder tokenBuilder;

    @Mock
    private UserDetails userDetails;
    @Mock
    private JwtClaimsSet claims;
    @Mock
    private JwtEncoderParameters encoderParameters;
    @Mock
    private Jwt jwt;

    private MockedStatic<JwtEncoderParameters> mockedEncoderParameters;

    @BeforeEach
    void setup() {
        mockedEncoderParameters = mockStatic(JwtEncoderParameters.class);
        service = new JwtTokenService(encoder, tokenBuilder);
    }

    @AfterEach
    void closeClosableStaticMocks() {
        mockedEncoderParameters.close();
    }

    @Test
    void generateJwtToken() {
        String tokenString = "testTokenString value";

        when(tokenBuilder.buildBy(userDetails))
                .thenReturn(claims);
        mockedEncoderParameters.when(
                        () -> JwtEncoderParameters.from(claims))
                .thenReturn(encoderParameters);
        when(encoder.encode(encoderParameters))
                .thenReturn(jwt);
        when(jwt.getTokenValue())
                .thenReturn(tokenString);

        String actual = service.generateJwtToken(userDetails);

        assertEquals(tokenString, actual);
    }
}
