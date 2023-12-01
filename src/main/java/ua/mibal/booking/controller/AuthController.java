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

package ua.mibal.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.mibal.booking.model.dto.auth.AuthResponseDto;
import ua.mibal.booking.model.dto.auth.ForgetPasswordDto;
import ua.mibal.booking.model.dto.auth.RegistrationDto;
import ua.mibal.booking.model.entity.User;
import ua.mibal.booking.service.AuthService;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponseDto login(@AuthenticationPrincipal User user) {
        return authService.token(user);
    }

    @PostMapping("/register")
    public void register(@Valid @RequestBody RegistrationDto registrationDto) {
        authService.register(registrationDto);
    }

    @PostMapping("/activate")
    public void activate(@RequestParam("code") String activationCode) {
        authService.activate(activationCode);
    }

    @GetMapping("/forget")
    public void resetPassword(@RequestParam("email") String email) {
        authService.restore(email);
    }

    @PostMapping("/forget/new")
    public void newPassword(@RequestParam("code") String activationCode,
                            @Valid @RequestBody ForgetPasswordDto forgetPasswordDto) {
        authService.newPassword(activationCode, forgetPasswordDto);
    }
}
