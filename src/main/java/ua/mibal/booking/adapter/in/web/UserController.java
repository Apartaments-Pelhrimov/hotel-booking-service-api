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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.mibal.booking.adapter.in.web.model.UserAccountDto;
import ua.mibal.booking.adapter.in.web.model.UserDto;
import ua.mibal.booking.adapter.in.web.security.annotation.UserAllowed;
import ua.mibal.booking.application.UserService;
import ua.mibal.booking.application.dto.request.ChangeNotificationSettingsDto;
import ua.mibal.booking.application.dto.request.ChangePasswordDto;
import ua.mibal.booking.application.dto.request.ChangeUserDetailsDto;
import ua.mibal.booking.application.dto.request.DeleteMeDto;
import ua.mibal.booking.application.mapper.UserMapper;
import ua.mibal.booking.domain.User;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@RestController
@UserAllowed
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/me")
    public UserDto getOne(Authentication authentication) {
        User user = userService.getOne(authentication.getName());
        return userMapper.toDto(user);
    }

    @GetMapping("/me/account")
    public UserAccountDto getAccount(Authentication authentication) {
        User user = userService.getOne(authentication.getName());
        return userMapper.toAccountDto(user);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Valid @RequestBody DeleteMeDto deleteMeDto,
                       Authentication authentication) {
        userService.delete(deleteMeDto, authentication.getName());
    }

    @PutMapping("/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto,
                               Authentication authentication) {
        userService.changePassword(changePasswordDto, authentication.getName());
    }

    @PatchMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeDetails(@Valid @RequestBody ChangeUserDetailsDto changeUserDetailsDto,
                              Authentication authentication) {
        userService.changeDetails(changeUserDetailsDto, authentication.getName());
    }

    @PatchMapping("/me/notifications")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeNotificationSettings(@RequestBody ChangeNotificationSettingsDto changeNotificationSettingsDto,
                                           Authentication authentication) {
        userService.changeNotificationSettings(changeNotificationSettingsDto, authentication.getName());
    }
}
