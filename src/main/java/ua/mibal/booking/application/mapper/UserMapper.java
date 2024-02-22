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

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ua.mibal.booking.application.dto.ChangeNotificationSettingsForm;
import ua.mibal.booking.application.dto.ChangeUserForm;
import ua.mibal.booking.application.dto.RegistrationForm;
import ua.mibal.booking.domain.NotificationSettings;
import ua.mibal.booking.domain.User;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Mapper(componentModel = SPRING,
        injectionStrategy = CONSTRUCTOR,
        uses = PhoneMapper.class)
public interface UserMapper {

    @Mapping(target = "password", source = "encodedPassword")
    User assemble(RegistrationForm registrationForm, String encodedPassword);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void change(@MappingTarget User user, ChangeUserForm changeUserForm);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void changeNotificationSettings(NotificationSettings notificationSettings,
                                    ChangeNotificationSettingsForm changeNotificationSettingsForm);
}
