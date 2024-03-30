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

package ua.mibal.booking.adapter.in.web.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.mibal.booking.adapter.in.web.controller.user.docs.UserControllerDocs;
import ua.mibal.booking.adapter.in.web.mapper.UserDtoMapper;
import ua.mibal.booking.adapter.in.web.model.ChangePasswordDto;
import ua.mibal.booking.adapter.in.web.model.DeleteMeDto;
import ua.mibal.booking.adapter.in.web.model.UserAccountDto;
import ua.mibal.booking.adapter.in.web.model.UserDto;
import ua.mibal.booking.adapter.in.web.security.annotation.UserAllowed;
import ua.mibal.booking.application.UserService;
import ua.mibal.booking.application.model.ChangeNotificationSettingsForm;
import ua.mibal.booking.application.model.ChangeUserForm;
import ua.mibal.booking.domain.User;

import static org.springframework.http.HttpStatus.NO_CONTENT;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@UserAllowed
@RestController
@RequestMapping("/api/users")
public class UserController implements UserControllerDocs {
    private final UserService userService;
    private final UserDtoMapper userDtoMapper;

    @Override
    @GetMapping("/me")
    public UserDto getOne(Authentication authentication) {
        User user = userService.getOne(authentication.getName());
        return userDtoMapper.toDto(user);
    }

    @Override
    @GetMapping("/me/account")
    public UserAccountDto getAccount(Authentication authentication) {
        User user = userService.getOne(authentication.getName());
        return userDtoMapper.toAccountDto(user);
    }

    @Override
    @DeleteMapping("/me/account")
    @ResponseStatus(NO_CONTENT)
    public void deleteAccount(@Valid @RequestBody DeleteMeDto dto,
                              Authentication authentication) {
        userService.delete(authentication.getName(), dto.password());
    }

    @Override
    @PatchMapping("/me/account")
    @ResponseStatus(NO_CONTENT)
    public void changeAccount(@Valid @RequestBody ChangeUserForm form,
                              Authentication authentication) {
        userService.change(authentication.getName(), form);
    }

    @Override
    @PutMapping("/me/account/password")
    @ResponseStatus(NO_CONTENT)
    public void putPassword(@Valid @RequestBody ChangePasswordDto dto,
                            Authentication authentication) {
        userService.putPassword(authentication.getName(), dto.oldPassword(), dto.newPassword());
    }

    @Override
    @PatchMapping("/me/account/notifications")
    @ResponseStatus(NO_CONTENT)
    public void changeNotificationSettings(@RequestBody ChangeNotificationSettingsForm form,
                                           Authentication authentication) {
        userService.changeNotificationSettings(authentication.getName(), form);
    }
}
