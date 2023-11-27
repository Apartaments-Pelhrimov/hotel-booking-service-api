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

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.mibal.booking.model.dto.request.ChangePasswordDto;
import ua.mibal.booking.model.dto.request.ChangeUserDetailsDto;
import ua.mibal.booking.model.dto.request.DeleteMeDto;
import ua.mibal.booking.model.dto.response.UserDto;
import ua.mibal.booking.service.UserService;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@RequiredArgsConstructor
@RestController
@RolesAllowed("USER")
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public UserDto getMe(Authentication authentication) {
        return userService.getOneByAuthentication(authentication);
    }

    @DeleteMapping("/me")
    public void deleteMe(@RequestBody DeleteMeDto deleteMeDto,
                         Authentication authentication) {
        userService.deleteByAuthentication(deleteMeDto, authentication);
    }

    @PutMapping("/me/password")
    public void changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto,
                               Authentication authentication) {
        userService.changePasswordByAuthentication(changePasswordDto, authentication);
    }

    @PutMapping("/me")
    public void changeDetails(@Valid @RequestBody ChangeUserDetailsDto changeUserDetailsDto,
                              Authentication authentication) {
        userService.changeDetails(changeUserDetailsDto, authentication);
    }
}
