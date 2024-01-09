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

package ua.mibal.booking.model.mapper;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ua.mibal.booking.model.dto.request.ChangeNotificationSettingsDto;
import ua.mibal.booking.model.dto.request.ChangeUserDetailsDto;
import ua.mibal.booking.model.entity.User;
import ua.mibal.booking.model.entity.embeddable.NotificationSettings;
import ua.mibal.booking.model.entity.embeddable.Phone;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserMapperImpl.class)
@TestPropertySource(locations = "classpath:application.yaml")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserMapper_UnitTest {

    @Autowired
    private UserMapper userMapper;

    @Mock
    private User user;
    @Mock
    private Phone phone;
    @Mock
    private NotificationSettings notificationSettings;

    // TODO
    @Test
    void toEntity() {
    }

    @Test
    void toAuthResponse() {
    }

    @Test
    void toDto() {
    }

    @ParameterizedTest
    @CsvSource({
            "firstName, lastName, phone",
            "firstName, lastName, null",
            "firstName, null,     null",
            "null,      null,     null",
            "null,      null,     phone",
            "null,      lastName, phone",
            "null,      lastName, phone",
    })
    void update_User(String firstName, String lastName, String number) {
        when(user.getPhone()).thenReturn(phone);

        userMapper.update(user, new ChangeUserDetailsDto(firstName, lastName, number));

        if (firstName == null) {
            verify(user, never()).setFirstName(any());
        } else {
            verify(user, times(1)).setFirstName(firstName);
        }
        if (lastName == null) {
            verify(user, never()).setLastName(any());
        } else {
            verify(user, times(1)).setLastName(lastName);
        }
        if (number == null) {
            verify(user, never()).setPhone(any());
            verify(user, never()).getPhone();
            verifyNoInteractions(phone);
        } else {
            verify(phone, times(1)).setNumber(number);
        }
    }

    @ParameterizedTest
    @CsvSource({
            "true, false",
            "true, null",
            "null, null",
            "null, true",
    })
    void update_NotificationSettings(Boolean receiveOrderEmails, Boolean receiveNewsEmails) {
        userMapper.update(notificationSettings, new ChangeNotificationSettingsDto(receiveOrderEmails, receiveNewsEmails));

        if (receiveOrderEmails == null) {
            verify(notificationSettings, never()).setReceiveOrderEmails(any());
        } else {
            verify(notificationSettings, times(1)).setReceiveOrderEmails(receiveOrderEmails);
        }
        if (receiveNewsEmails == null) {
            verify(notificationSettings, never()).setReceiveNewsEmails(any());
        } else {
            verify(notificationSettings, times(1)).setReceiveNewsEmails(receiveNewsEmails);
        }
    }
}
