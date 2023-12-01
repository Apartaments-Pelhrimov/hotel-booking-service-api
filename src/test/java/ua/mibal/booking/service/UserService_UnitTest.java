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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    void getOneByEmailDto() {
        when(userRepository.findByEmail("existing_email"))
                .thenReturn(Optional.of(user));
        when(userMapper.toDto(user))
                .thenReturn(userDto);

        UserDto actual = assertDoesNotThrow(
                () -> service.getOneByEmailDto("existing_email")
        );

        assertEquals(userDto, actual);
    }

    @Test
    void getOneByEmailDto_should_throw_UserNotFoundException() {
        when(userRepository.findByEmail("not_existing_email"))
                .thenReturn(Optional.empty());

        UserNotFoundException e = assertThrows(
                UserNotFoundException.class,
                () -> service.getOneByEmailDto("not_existing_email")
        );
        assertEquals(
                new UserNotFoundException("not_existing_email").getMessage(),
                e.getMessage()
        );
    }

    @Test
    void getOneByEmail() {
        when(userRepository.findByEmail("existing_email"))
                .thenReturn(Optional.of(user));

        User actual = assertDoesNotThrow(
                () -> service.getOneByEmail("existing_email")
        );

        assertEquals(user, actual);
    }

    @Test
    void getOneByEmail_should_throw_UserNotFoundException() {
        when(userRepository.findByEmail("not_existing_email"))
                .thenReturn(Optional.empty());

        UserNotFoundException e = assertThrows(
                UserNotFoundException.class,
                () -> service.getOneByEmail("not_existing_email")
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

    @ParameterizedTest
    @CsvSource(value = {
            "name,name,phone",
            "null,name,phone",
            "null,null,phone",
            "null,null,null",
            "name,null,null",
            "name,name,null",
    }, nullValues = "null")
    void changeDetails_should_update_dynamic(String firstName, String lastName, String phone) {
        User emptyUser = new User();
        when(userRepository.findByEmail("email"))
                .thenReturn(Optional.of(emptyUser));

        service.changeDetails(new ChangeUserDetailsDto(firstName, lastName, phone), "email");

        if (firstName == null) assertNull(emptyUser.getFirstName());
        if (lastName == null) assertNull(emptyUser.getLastName());
        if (phone == null) assertNull(emptyUser.getPhone());
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

        if (order == null) verify(notificationSettings, never())
                .setReceiveOrderEmails(anyBoolean());
        if (news == null) verify(notificationSettings, never())
                .setReceiveNewsEmails(anyBoolean());
    }
}
