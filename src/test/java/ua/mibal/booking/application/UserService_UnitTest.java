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

package ua.mibal.booking.application;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.mibal.booking.application.component.FakePasswordEncoder;
import ua.mibal.booking.application.exception.EmailAlreadyExistsException;
import ua.mibal.booking.application.exception.IllegalPasswordException;
import ua.mibal.booking.application.exception.UserNotFoundException;
import ua.mibal.booking.application.mapper.UserMapper;
import ua.mibal.booking.application.model.ChangeNotificationSettingsForm;
import ua.mibal.booking.application.model.ChangeUserForm;
import ua.mibal.booking.application.model.RegistrationForm;
import ua.mibal.booking.application.port.jpa.FakeInMemoryUserRepository;
import ua.mibal.booking.domain.User;
import ua.mibal.test.annotation.UnitTest;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class UserService_UnitTest {

    private final FakeInMemoryUserRepository userRepository = new FakeInMemoryUserRepository();
    private final PasswordEncoder passwordEncoder = new FakePasswordEncoder();
    private final UserMapper userMapper = mock();

    private final UserService service = new UserService(userRepository, userMapper, passwordEncoder);

    private User user;
    private RegistrationForm registrationForm;
    private ChangeUserForm changeUserForm;
    private ChangeNotificationSettingsForm changeNotificationSettingsForm;

    @Test
    void getOne() {
        givenRepositoryWithUser("user@email");

        thenGetOneShouldReturnUser("user@email");
    }

    @Test
    void getOne_should_throw() {
        givenEmptyRepository();

        thenGetOneShouldThrowNotFoundException("user@email");
    }

    @Test
    void delete() {
        givenRepositoryWithUser("user@email");
        givenUserWithPassword("password");

        whenDeleteUserWithPassword("user@email", "password");

        thenUserDeleted("user@email");
    }

    @Test
    void delete_should_throw_IllegalPasswordException() {
        givenRepositoryWithUser("user@email");
        givenUserWithPassword("password");

        thenDeleteUserWithPasswordShouldThrow("user@email", "wrong_pass",
                IllegalPasswordException.class);
        thenUserExists("user@email");
    }

    @Test
    void delete_should_throw_NotFoundException() {
        givenEmptyRepository();

        thenDeleteUserWithPasswordShouldThrow("any_user@email", "any_pass",
                UserNotFoundException.class);
    }

    @Test
    void save() {
        givenRegistrationForm("user2@email", "password");

        whenSaveUserByForm();

        thenUserExists("user2@email");
    }

    @Test
    void save_should_throw_EmailAlreadyExistsException() {
        givenRepositoryWithUser("user@email");
        givenRegistrationForm("user@email", "password");

        thenSaveUserByFormShouldThrowEmailAlreadyExistsException();
    }

    @Test
    void putPassword() {
        givenRepositoryWithUser("user@email");
        givenUserWithPassword("old_pass");

        whenPutPassword("user@email", "old_pass", "new_pass");

        thenUserPasswordShouldBe("user@email", "new_pass");
    }

    @Test
    void putPassword_should_throw_UserNotFoundException() {
        givenEmptyRepository();

        thenPutPasswordShouldThrowUserNotFoundException(
                "user@email", "old_pass", "new_pass",
                UserNotFoundException.class
        );
    }

    @Test
    void putPassword_should_throw_IllegalPasswordException() {
        givenRepositoryWithUser("user@email");
        givenUserWithPassword("old_pass");

        thenPutPasswordShouldThrowUserNotFoundException(
                "user@email", "wrong_pass", "new_pass",
                IllegalPasswordException.class
        );
    }

    @Test
    void change() {
        givenRepositoryWithUser("user@email");
        givenChangeUserForm();

        whenChangeUser("user@email");

        thenMapperChangeShouldBeCalled("user@email");
    }

    @Test
    void change_should_throw() {
        givenEmptyRepository();
        givenChangeUserForm();

        thenChangeUserShouldThrowException("user@email");
    }

    @Test
    void changeNotificationSettings() {
        givenRepositoryWithUser("user@email");
        givenСhangeNotificationSettingsForm();

        whenChangeNotificationSettingsOfUser("user@email");

        thenMapperChangeNotificationSettingsShouldBeCalled("user@email");
    }

    @Test
    void changeNotificationSettings_should_throw() {
        givenEmptyRepository();
        givenСhangeNotificationSettingsForm();

        thenChangeNotificationSettingsOfUserShouldThrowException("user@email");
    }

    @Test
    void clearNotEnabledWithNoTokens() {
        givenRepositoryWithNotEnabledUsers(5);
        givenRepositoryWithUser("user@email");

        thenClearNotEnabledWithNoTokensShouldReturn(5);
    }

    private void givenChangeUserForm() {
        changeUserForm = Instancio.of(ChangeUserForm.class).create();
    }

    private void givenRepositoryWithUser(String username) {
        user = Instancio.of(User.class)
                .set(field(User::getEmail), username)
                .create();
        userRepository.save(user);
    }

    private void givenEmptyRepository() {
        userRepository.deleteAll();
    }

    private void givenUserWithPassword(String password) {
        user.setPassword(password);
    }

    private void givenRegistrationForm(String username, String password) {
        registrationForm = Instancio.of(RegistrationForm.class)
                .set(field(RegistrationForm::email), username)
                .set(field(RegistrationForm::password), password)
                .create();
        User newUser = Instancio.of(User.class)
                .set(field(User::getEmail), username)
                .set(field(User::getPassword), password)
                .create();
        when(userMapper.assemble(registrationForm, password))
                .thenReturn(newUser);
    }

    private void givenСhangeNotificationSettingsForm() {
        changeNotificationSettingsForm = Instancio.of(ChangeNotificationSettingsForm.class)
                .create();
    }

    private void givenRepositoryWithNotEnabledUsers(int count) {
        for (int i = 0; i < count; i++) {
            userRepository.save(Instancio.of(User.class)
                    .set(field(User::isEnabled), false)
                    .set(field(User::getToken), null)
                    .create());
        }
    }

    private void whenChangeNotificationSettingsOfUser(String username) {
        service.changeNotificationSettings(username, changeNotificationSettingsForm);
    }

    private void whenSaveUserByForm() {
        service.save(registrationForm);
    }

    private void whenDeleteUserWithPassword(String username, String password) {
        service.delete(username, password);
    }

    private void whenPutPassword(String username, String oldPass, String newPass) {
        service.putPassword(username, oldPass, newPass);
    }

    private void whenChangeUser(String username) {
        service.change(username, changeUserForm);
    }

    private void thenMapperChangeShouldBeCalled(String s) {
        verify(userMapper, times(1))
                .change(user, changeUserForm);
    }

    private void thenUserPasswordShouldBe(String username, String password) {
        User user = userRepository.findByEmail(username)
                .orElseThrow();
        assertEquals(password, user.getPassword());
    }

    private void thenGetOneShouldReturnUser(String username) {
        User actual = service.getOne(username);
        assertEquals(user, actual);
    }

    private void thenGetOneShouldThrowNotFoundException(String username) {
        assertThrows(
                UserNotFoundException.class,
                () -> service.getOne(username)
        );
    }

    private void thenUserDeleted(String username) {
        boolean userExists = userRepository.existsByEmail(username);
        assertFalse(userExists);
    }

    private void thenDeleteUserWithPasswordShouldThrow(String username, String password, Class<? extends Throwable> exceptionClass) {
        assertThrows(
                exceptionClass,
                () -> whenDeleteUserWithPassword(username, password)
        );
    }

    private void thenUserExists(String username) {
        boolean userExists = userRepository.existsByEmail(username);
        assertTrue(userExists);
    }

    private void thenSaveUserByFormShouldThrowEmailAlreadyExistsException() {
        assertThrows(
                EmailAlreadyExistsException.class,
                this::whenSaveUserByForm
        );
    }

    private void thenPutPasswordShouldThrowUserNotFoundException(String username, String oldPass, String newPass,
                                                                 Class<? extends Throwable> exceptionClass) {
        assertThrows(
                exceptionClass,
                () -> whenPutPassword(username, oldPass, newPass)
        );
    }

    private void thenChangeUserShouldThrowException(String username) {
        assertThrows(
                UserNotFoundException.class,
                () -> whenChangeUser(username)
        );
    }

    private void thenMapperChangeNotificationSettingsShouldBeCalled(String username) {
        verify(userMapper, times(1))
                .changeNotificationSettings(user.getNotificationSettings(), changeNotificationSettingsForm);
    }

    private void thenChangeNotificationSettingsOfUserShouldThrowException(String username) {
        assertThrows(
                UserNotFoundException.class,
                () -> whenChangeNotificationSettingsOfUser(username)
        );
    }

    private void thenClearNotEnabledWithNoTokensShouldReturn(int count) {
        int actual = service.clearNotEnabledWithNoTokens();
        assertEquals(count, actual);
    }
}
