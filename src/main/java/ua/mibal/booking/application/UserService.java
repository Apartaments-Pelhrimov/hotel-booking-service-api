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
import ua.mibal.booking.application.dto.ChangeNotificationSettingsForm;
import ua.mibal.booking.application.dto.ChangeUserForm;
import ua.mibal.booking.application.dto.RegistrationForm;
import ua.mibal.booking.application.exception.IllegalPasswordException;
import ua.mibal.booking.application.exception.UserNotFoundException;
import ua.mibal.booking.application.mapper.UserMapper;
import ua.mibal.booking.application.port.jpa.UserRepository;
import ua.mibal.booking.domain.Photo;
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

    public boolean isExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void delete(String email, String password) {
        validatePassword(password, email);
        userRepository.deleteByEmail(email);
    }

    public User save(RegistrationForm form) {
        String encodedPass = passwordEncoder.encode(form.password());
        User user = userMapper.assemble(form, encodedPass);
        return userRepository.save(user);
    }

    public void putPassword(String email, String oldPassword, String newPassword) {
        validatePassword(oldPassword, email);
        String newEncodedPassword = passwordEncoder.encode(newPassword);
        userRepository.updateUserPasswordByEmail(newEncodedPassword, email);
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

    @Transactional
    public void setNewPasswordForUser(Long userId, String newPassword) {
        User user = getOneById(userId);
        String newEncodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(newEncodedPassword);
    }

    @Transactional
    public void activateUserById(Long userId) {
        User user = getOneById(userId);
        user.enable();
    }

    public void changeUserPhoto(String email, String key) {
        Photo photo = new Photo(key);
        userRepository.updateUserPhotoByEmail(photo, email);
    }

    public int clearNotEnabledWithNoTokens() {
        return userRepository.deleteNotEnabledWithNoTokens();
    }

    private User getOneById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private void validatePassword(String password, String email) {
        String encodedPassword = userRepository.findPasswordByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        if (!passwordEncoder.matches(password, encodedPassword)) {
            throw new IllegalPasswordException();
        }
    }
}
