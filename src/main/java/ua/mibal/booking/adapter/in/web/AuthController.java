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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.mibal.booking.adapter.in.web.mapper.AuthDtoMapper;
import ua.mibal.booking.adapter.in.web.model.LoginDto;
import ua.mibal.booking.adapter.in.web.model.NewPasswordDto;
import ua.mibal.booking.adapter.in.web.model.TokenDto;
import ua.mibal.booking.application.AuthService;
import ua.mibal.booking.application.dto.RegistrationForm;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final AuthDtoMapper authDtoMapper;

    @PostMapping("/login")
    public TokenDto login(@Valid @RequestBody LoginDto loginDto) {
        String token = authService.login(loginDto.username(), loginDto.password());
        return authDtoMapper.toTokenDto(token);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody RegistrationForm registrationForm) {
        authService.register(registrationForm);
    }

    @PostMapping("/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activateNewAccount(@RequestParam("token") String activationToken) {
        authService.activateNewAccountBy(activationToken);
    }

    @GetMapping("/forget")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void forgetPassword(@RequestParam("email") String email) {
        authService.restore(email);
    }

    @PutMapping("/forget/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setNewPassword(@RequestParam("token") String activationToken,
                               @Valid @RequestBody NewPasswordDto newPasswordDto) {
        authService.setNewPassword(activationToken, newPasswordDto.password());
    }
}
