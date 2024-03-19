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

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.mibal.booking.application.exception.EmailAlreadyExistsException;
import ua.mibal.booking.application.exception.IllegalPasswordException;
import ua.mibal.booking.application.exception.UserNotFoundException;
import ua.mibal.booking.application.mapper.UserMapper;
import ua.mibal.booking.application.model.ChangeNotificationSettingsForm;
import ua.mibal.booking.application.model.ChangeUserForm;
import ua.mibal.booking.application.model.RegistrationForm;
import ua.mibal.booking.application.port.jpa.UserRepository;
import ua.mibal.booking.domain.User;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public User getOne(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    public void delete(String email, String password) {
        User user = getOne(email);
        validatePassword(user.getPassword(), password);
        userRepository.delete(user);
    }

    public User save(RegistrationForm form) {
        validateEmailDoesNotExist(form.email());
        String encodedPass = passwordEncoder.encode(form.password());
        User user = userMapper.assemble(form, encodedPass);
        return userRepository.save(user);
    }

    @Transactional
    public void putPassword(String email, String oldPassword, String newPassword) {
        User user = getOne(email);
        validatePassword(user.getPassword(), oldPassword);
        String newEncodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(newEncodedPassword);
    }

    @Transactional
    public void change(String email, ChangeUserForm form) {
        User user = getOne(email);
        userMapper.change(user, form);
    }

    @Transactional
    public void changeNotificationSettings(String email, ChangeNotificationSettingsForm form) {
        User user = getOne(email);
        userMapper.changeNotificationSettings(user.getNotificationSettings(), form);
    }

    public int clearNotEnabledWithNoTokens() {
        return userRepository.deleteNotEnabledWithNoTokens();
    }

    private void validatePassword(String original, String attempt) {
        if (!passwordEncoder.matches(attempt, original)) {
            throw new IllegalPasswordException();
        }
    }

    private void validateEmailDoesNotExist(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }
    }
}
