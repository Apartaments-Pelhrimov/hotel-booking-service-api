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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ua.mibal.booking.application.model.ChangeNotificationSettingsForm;
import ua.mibal.booking.application.model.ChangeUserForm;
import ua.mibal.booking.application.model.RegistrationForm;
import ua.mibal.booking.domain.NotificationSettings;
import ua.mibal.booking.domain.User;
import ua.mibal.test.annotation.UnitTest;

import static java.util.Optional.empty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static ua.mibal.booking.domain.NotificationSettings.DEFAULT;
import static ua.mibal.booking.domain.Role.USER;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class UserMapper_UnitTest {

    private UserMapper mapper = new UserMapperImpl();

    @ParameterizedTest
    @InstancioSource
    void assemble(RegistrationForm registrationForm, String encodedPassword) {
        User user = mapper.assemble(registrationForm, encodedPassword);

        assertThat(user.getId(), nullValue());
        assertThat(user.getFirstName(), is(registrationForm.firstName()));
        assertThat(user.getLastName(), is(registrationForm.lastName()));
        assertThat(user.getEmail(), is(registrationForm.email()));
        assertThat(user.getPassword(), is(encodedPassword));
        assertThat(user.getPhone().getNumber(), is(registrationForm.phone()));
        assertThat(user.getPhoto(), is(empty()));
        assertThat(user.getNotificationSettings(), is(DEFAULT));
        assertThat(user.isEnabled(), is(false));
        assertThat(user.getRole(), is(USER));
        assertThat(user.getReservations(), hasSize(0));
        assertThat(user.getComments(), hasSize(0));
        assertThat(user.getToken(), nullValue());
    }

    @Test
    void assemble_should_return_null_if_arguments_are_null() {
        User user = mapper.assemble(null, null);

        assertThat(user, nullValue());
    }

    @ParameterizedTest
    @MethodSource("ua.mibal.test.util.DataGenerator#testUsers")
    void change(User source, ChangeUserForm userChanges) {

        String expectedFirstName = userChanges.firstName() == null
                ? source.getFirstName()
                : userChanges.firstName();
        String expectedLastName = userChanges.lastName() == null
                ? source.getLastName()
                : userChanges.lastName();
        String expectedPhoneNumber = userChanges.phone() == null
                ? source.getPhone().getNumber()
                : userChanges.phone();

        mapper.change(source, userChanges);

        assertThat(source.getFirstName(), is(expectedFirstName));
        assertThat(source.getLastName(), is(expectedLastName));
        assertThat(source.getPhone().getNumber(), is(expectedPhoneNumber));
    }

    @ParameterizedTest
    @MethodSource("ua.mibal.test.util.DataGenerator#testNotificationSettings")
    void changeNotificationSettings(NotificationSettings source,
                                    ChangeNotificationSettingsForm userChanges) {
        boolean expectedOrderEmails = userChanges.receiveOrderEmails() == null
                ? source.isReceiveOrderEmails()
                : userChanges.receiveOrderEmails();
        boolean expectedNewsEmails = userChanges.receiveNewsEmails() == null
                ? source.isReceiveNewsEmails()
                : userChanges.receiveNewsEmails();

        mapper.changeNotificationSettings(source, userChanges);

        assertThat(source.isReceiveOrderEmails(), is(expectedOrderEmails));
        assertThat(source.isReceiveNewsEmails(), is(expectedNewsEmails));
    }
}
