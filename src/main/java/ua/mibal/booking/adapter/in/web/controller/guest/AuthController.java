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

package ua.mibal.booking.adapter.in.web.controller.guest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.mibal.booking.adapter.in.web.mapper.AuthDtoMapper;
import ua.mibal.booking.adapter.in.web.model.LoginDto;
import ua.mibal.booking.adapter.in.web.model.TokenDto;
import ua.mibal.booking.application.AuthService;
import ua.mibal.booking.application.model.RegistrationForm;
import ua.mibal.booking.application.model.RestorePasswordForm;
import ua.mibal.booking.application.model.SetPasswordForm;
import ua.mibal.booking.application.model.TokenForm;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

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
    @ResponseStatus(CREATED)
    public void register(@Valid @RequestBody RegistrationForm registrationForm) {
        authService.register(registrationForm);
    }

    @PostMapping("/activate")
    @ResponseStatus(NO_CONTENT)
    public void activateNewAccount(@Valid @RequestBody TokenForm tokenForm) {
        authService.activateNewAccountBy(tokenForm);
    }

    @PostMapping("/restore")
    @ResponseStatus(NO_CONTENT)
    public void restorePassword(@Valid @RequestBody RestorePasswordForm restorePasswordForm) {
        authService.restore(restorePasswordForm);
    }

    @PutMapping("/restore/password")
    @ResponseStatus(NO_CONTENT)
    public void setNewPassword(@Valid @RequestBody SetPasswordForm setPasswordForm) {
        authService.setNewPassword(setPasswordForm);
    }
}
