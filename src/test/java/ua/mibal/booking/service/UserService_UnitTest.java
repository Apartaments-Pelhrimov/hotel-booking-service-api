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

package ua.mibal.booking.service;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ua.mibal.booking.model.dto.auth.RegistrationDto;
import ua.mibal.booking.model.dto.request.ChangeNotificationSettingsDto;
import ua.mibal.booking.model.dto.request.ChangePasswordDto;
import ua.mibal.booking.model.dto.request.ChangeUserDetailsDto;
import ua.mibal.booking.model.dto.request.DeleteMeDto;
import ua.mibal.booking.model.dto.response.UserDto;
import ua.mibal.booking.model.entity.User;
import ua.mibal.booking.model.entity.embeddable.NotificationSettings;
import ua.mibal.booking.model.exception.IllegalPasswordException;
import ua.mibal.booking.model.exception.entity.UserNotFoundException;
import ua.mibal.booking.model.mapper.UserMapper;
import ua.mibal.booking.repository.UserRepository;
import ua.mibal.booking.testUtils.DataGenerator;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserService.class)
@TestPropertySource(locations = "classpath:application.yaml")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserService_UnitTest {

    @Autowired
    private UserService service;

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private UserMapper userMapper;
    @MockBean
    private PasswordEncoder passwordEncoder;

    @Mock
    private User user;
    @Mock
    private UserDto userDto;
    @Mock
    private RegistrationDto registrationDto;
    @Mock
    private ChangePasswordDto changePasswordDto;
    @Mock
    private ChangeUserDetailsDto changeUserDetailsDto;
    @Mock
    private ChangeNotificationSettingsDto changeNotificationSettingsDto;
    @Mock
    private NotificationSettings notificationSettings;

    @Test
    void getOneDto() {
        when(userRepository.findByEmail("existing_email"))
                .thenReturn(Optional.of(user));
        when(userMapper.toDto(user))
                .thenReturn(userDto);

        UserDto actual = assertDoesNotThrow(
                () -> service.getOneDto("existing_email")
        );

        assertEquals(userDto, actual);
    }

    @Test
    void getOneDto_should_throw_UserNotFoundException() {
        when(userRepository.findByEmail("not_existing_email"))
                .thenReturn(Optional.empty());

        UserNotFoundException e = assertThrows(
                UserNotFoundException.class,
                () -> service.getOneDto("not_existing_email")
        );
        assertEquals(
                new UserNotFoundException("not_existing_email").getMessage(),
                e.getMessage()
        );
    }

    @Test
    void getOne() {
        when(userRepository.findByEmail("existing_email"))
                .thenReturn(Optional.of(user));

        User actual = assertDoesNotThrow(
                () -> service.getOne("existing_email")
        );

        assertEquals(user, actual);
    }

    @Test
    void getOne_should_throw_UserNotFoundException() {
        when(userRepository.findByEmail("not_existing_email"))
                .thenReturn(Optional.empty());

        UserNotFoundException e = assertThrows(
                UserNotFoundException.class,
                () -> service.getOne("not_existing_email")
        );
        assertEquals(
                new UserNotFoundException("not_existing_email").getMessage(),
                e.getMessage()
        );
    }

    @ParameterizedTest
    @CsvSource({"true", "false"})
    void isExistsByEmail(boolean value) {
        when(userRepository.existsByEmail("email"))
                .thenReturn(value);

        boolean actual = service.isExistsByEmail("email");

        assertEquals(value, actual);
    }

    @Test
    void delete() {
        DeleteMeDto deleteMeDto = new DeleteMeDto("password");
        when(userRepository.findPasswordByEmail("email"))
                .thenReturn(Optional.of("password"));
        when(passwordEncoder.matches("password", "password"))
                .thenReturn(true);

        service.delete(deleteMeDto, "email");

        verify(userRepository, times(1))
                .deleteByEmail("email");
    }

    @Test
    void delete_should_throw_UserNotFoundException() {
        DeleteMeDto deleteMeDto = new DeleteMeDto("password");
        when(userRepository.findPasswordByEmail("not_existing_email"))
                .thenReturn(Optional.empty());
        verifyNoMoreInteractions(userRepository, passwordEncoder);

        UserNotFoundException e = assertThrows(
                UserNotFoundException.class,
                () -> service.delete(deleteMeDto, "not_existing_email")
        );
        assertEquals(
                new UserNotFoundException("not_existing_email").getMessage(),
                e.getMessage()
        );
    }

    @Test
    void delete_should_throw_IllegalPasswordException() {
        DeleteMeDto deleteMeDto = new DeleteMeDto("password");
        when(userRepository.findPasswordByEmail("email"))
                .thenReturn(Optional.of("password"));
        when(passwordEncoder.matches("password", "password"))
                .thenReturn(false);
        verifyNoMoreInteractions(userRepository);

        IllegalPasswordException e = assertThrows(
                IllegalPasswordException.class,
                () -> service.delete(deleteMeDto, "email")
        );
        assertEquals(
                new IllegalPasswordException().getMessage(),
                e.getMessage()
        );
    }

    @Test
    void save() {
        when(userMapper.toEntity(registrationDto, "password"))
                .thenReturn(user);
        when(userRepository.save(user))
                .thenReturn(user);

        service.save(registrationDto, "password");

        verify(userRepository, times(1))
                .save(user);
    }

    @Test
    void changePassword() {
        when(changePasswordDto.oldPassword()).thenReturn("password");
        when(changePasswordDto.newPassword()).thenReturn("newpass");
        when(userRepository.findPasswordByEmail("email"))
                .thenReturn(Optional.of("password"));
        when(passwordEncoder.matches("password", "password"))
                .thenReturn(true);
        when(passwordEncoder.encode("newpass"))
                .thenReturn("encoded_newpass");

        assertDoesNotThrow(
                () -> service.changePassword(changePasswordDto, "email")
        );

        verify(userRepository, times(1))
                .updateUserPasswordByEmail("encoded_newpass", "email");
    }

    @Test
    void changePassword_should_throw_UserNotFoundException() {
        when(userRepository.findPasswordByEmail("not_existing"))
                .thenReturn(Optional.empty());
        verifyNoMoreInteractions(userRepository);

        UserNotFoundException e = assertThrows(
                UserNotFoundException.class,
                () -> service.changePassword(changePasswordDto, "not_existing")
        );
        assertEquals(
                new UserNotFoundException("not_existing").getMessage(),
                e.getMessage()
        );
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void changePassword_should_throw_IllegalPasswordException() {
        when(changePasswordDto.oldPassword()).thenReturn("oldPassword");
        when(userRepository.findPasswordByEmail("email"))
                .thenReturn(Optional.of("password"));
        when(passwordEncoder.matches("oldPassword", "password"))
                .thenReturn(false);
        verifyNoMoreInteractions(userRepository, passwordEncoder);

        IllegalPasswordException e = assertThrows(
                IllegalPasswordException.class,
                () -> service.changePassword(changePasswordDto, "email")
        );
        assertEquals(
                new IllegalPasswordException().getMessage(),
                e.getMessage()
        );
    }

    @Test
    void changeDetails_should_update_dynamic() {
        User test = DataGenerator.testUser();
        when(userRepository.findByEmail("email")).thenReturn(Optional.of(test));

        service.changeDetails(changeUserDetailsDto, "email");

        verify(userMapper, times(1)).update(test, changeUserDetailsDto);
    }

    @Test
    void changeDetails_should_throw_UserNotFoundException() {
        when(userRepository.findByEmail("not_existing_email"))
                .thenReturn(Optional.empty());

        UserNotFoundException e = assertThrows(
                UserNotFoundException.class,
                () -> service.changeDetails(changeUserDetailsDto, "not_existing_email")
        );
        assertEquals(
                new UserNotFoundException("not_existing_email").getMessage(),
                e.getMessage()
        );
    }

    @Test
    void changeNotificationSettings_should_throw_UserNotFoundException() {
        when(userRepository.findByEmail("not_existing_email"))
                .thenReturn(Optional.empty());

        UserNotFoundException e = assertThrows(
                UserNotFoundException.class,
                () -> service.changeNotificationSettings(changeNotificationSettingsDto, "not_existing_email")
        );
        assertEquals(
                new UserNotFoundException("not_existing_email").getMessage(),
                e.getMessage()
        );
    }

    @ParameterizedTest
    @CsvSource(value = {
            "true,true",
            "null,true",
            "null,null",
            "true,null",
    }, nullValues = "null")
    void changeNotificationSettings_should_update_dynamic(Boolean order, Boolean news) {
        when(userRepository.findByEmail("email"))
                .thenReturn(Optional.of(user));
        when(user.getNotificationSettings())
                .thenReturn(notificationSettings);

        service.changeNotificationSettings(new ChangeNotificationSettingsDto(order, news), "email");

        if (order == null) {
            verify(notificationSettings, never())
                    .setReceiveOrderEmails(anyBoolean());
        }
        if (news == null) {
            verify(notificationSettings, never())
                    .setReceiveNewsEmails(anyBoolean());
        }
    }
}
