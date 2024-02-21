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

package ua.mibal.booking.application.mapper;

import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import ua.mibal.booking.adapter.in.web.model.UserDto;
import ua.mibal.booking.application.dto.auth.RegistrationDto;
import ua.mibal.booking.application.dto.auth.TokenDto;
import ua.mibal.booking.application.dto.request.ChangeNotificationSettingsDto;
import ua.mibal.booking.application.dto.request.ChangeUserDetailsDto;
import ua.mibal.booking.application.mapper.linker.UserPhotoLinker;
import ua.mibal.booking.domain.NotificationSettings;
import ua.mibal.booking.domain.Phone;
import ua.mibal.booking.domain.User;
import ua.mibal.test.annotation.UnitTest;

import static java.util.Optional.empty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static ua.mibal.booking.domain.NotificationSettings.DEFAULT;
import static ua.mibal.booking.domain.Role.USER;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class UserMapper_UnitTest {

    private UserMapper mapper;

    @Mock
    private UserPhotoLinker photoLinker;
    @Mock
    private PhoneMapper phoneMapper;

    @Mock
    private Phone mappedPhone;

    @BeforeEach
    public void setup() {
        mapper = new UserMapperImpl(photoLinker, phoneMapper);
    }

    @ParameterizedTest
    @InstancioSource
    void toEntity(RegistrationDto registrationDto, String encodedPassword) {
        when(phoneMapper.toNumberString(registrationDto.phone()))
                .thenReturn(mappedPhone);

        User user = mapper.toEntity(registrationDto, encodedPassword);

        assertThat(user.getId(), nullValue());
        assertThat(user.getFirstName(), is(registrationDto.firstName()));
        assertThat(user.getLastName(), is(registrationDto.lastName()));
        assertThat(user.getEmail(), is(registrationDto.email()));
        assertThat(user.getPassword(), is(encodedPassword));
        assertThat(user.getPhone(), is(mappedPhone));
        assertThat(user.getPhoto(), is(empty()));
        assertThat(user.getNotificationSettings(), is(DEFAULT));
        assertThat(user.isEnabled(), is(false));
        assertThat(user.getRole(), is(USER));
        assertThat(user.getReservations(), hasSize(0));
        assertThat(user.getComments(), hasSize(0));
        assertThat(user.getToken(), nullValue());
    }

    @Test
    void toEntity_should_return_null_if_arguments_are_null() {
        User user = mapper.toEntity(null, null);

        assertThat(user, nullValue());
    }

    @ParameterizedTest
    @InstancioSource
    void toToken(User user, String jwtToken) {
        TokenDto tokenDto = mapper.toToken(user, jwtToken);

        assertThat(tokenDto.firstName(), is(user.getFirstName()));
        assertThat(tokenDto.lastName(), is(user.getLastName()));
        assertThat(tokenDto.token(), is(jwtToken));
    }

    @Test
    void toToken_should_return_null_if_arguments_are_null() {
        TokenDto tokenDto = mapper.toToken(null, null);

        assertThat(tokenDto, nullValue());
    }

    @ParameterizedTest
    @InstancioSource
    void toDto(User user, String photoUrl) {
        when(photoLinker.toLink(user))
                .thenReturn(photoUrl);
        when(phoneMapper.toNumberString(user.getPhone()))
                .thenReturn(user.getPhone().getNumber());

        UserDto userDto = mapper.toDto(user);

        assertThat(userDto.firstName(), is(user.getFirstName()));
        assertThat(userDto.lastName(), is(user.getLastName()));
        assertThat(userDto.photo(), is(photoUrl));
        assertThat(userDto.role(), is(user.getRole()));
    }

    @Test
    void toDto_should_return_null_if_arguments_are_null() {
        UserDto userDto = mapper.toDto(null);

        assertThat(userDto, nullValue());
    }

    @ParameterizedTest
    @MethodSource("ua.mibal.test.util.DataGenerator#testUsers")
    void update_User(User source, ChangeUserDetailsDto userChanges) {
        when(phoneMapper.toNumberString(userChanges.phone()))
                .thenReturn(mappedPhone);

        String expectedFirstName = userChanges.firstName() == null
                ? source.getFirstName()
                : userChanges.firstName();
        String expectedLastName = userChanges.lastName() == null
                ? source.getLastName()
                : userChanges.lastName();
        Phone expectedPhone = userChanges.phone() == null
                ? source.getPhone()
                : mappedPhone;

        mapper.update(source, userChanges);

        assertThat(source.getFirstName(), is(expectedFirstName));
        assertThat(source.getLastName(), is(expectedLastName));
        assertThat(source.getPhone(), is(expectedPhone));
    }

    @ParameterizedTest
    @MethodSource("ua.mibal.test.util.DataGenerator#testNotificationSettings")
    void update_NotificationSettings(NotificationSettings source,
                                     ChangeNotificationSettingsDto userChanges) {
        boolean expectedOrderEmails = userChanges.receiveOrderEmails() == null
                ? source.isReceiveOrderEmails()
                : userChanges.receiveOrderEmails();
        boolean expectedNewsEmails = userChanges.receiveNewsEmails() == null
                ? source.isReceiveNewsEmails()
                : userChanges.receiveNewsEmails();

        mapper.update(source, userChanges);

        assertThat(source.isReceiveOrderEmails(), is(expectedOrderEmails));
        assertThat(source.isReceiveNewsEmails(), is(expectedNewsEmails));
    }
}
