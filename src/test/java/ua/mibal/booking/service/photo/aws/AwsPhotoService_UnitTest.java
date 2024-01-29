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

package ua.mibal.booking.service.photo.aws;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.multipart.MultipartFile;
import ua.mibal.booking.model.entity.Apartment;
import ua.mibal.booking.model.entity.embeddable.Photo;
import ua.mibal.booking.service.ApartmentService;
import ua.mibal.booking.service.UserService;
import ua.mibal.booking.service.photo.aws.components.AwsStorage;
import ua.mibal.booking.service.photo.aws.model.ApartmentAwsPhoto;
import ua.mibal.booking.service.photo.aws.model.UserAwsPhoto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AwsPhotoService_UnitTest {

    private AwsPhotoService service;

    @Mock
    private AwsStorage storage;
    @Mock
    private UserService userService;
    @Mock
    private ApartmentService apartmentService;

    @Mock
    private MultipartFile photoFile;
    @Mock
    private Apartment apartment;
    @Mock
    private UserAwsPhoto userPhoto;
    @Mock
    private ApartmentAwsPhoto apartmentPhoto;

    private MockedStatic<UserAwsPhoto> mockedUserAwsPhoto;
    private MockedStatic<ApartmentAwsPhoto> mockedApartmentAwsPhoto;


    @BeforeEach
    void setup() {
        mockedUserAwsPhoto = mockStatic(UserAwsPhoto.class);
        mockedApartmentAwsPhoto = mockStatic(ApartmentAwsPhoto.class);
        service = new AwsPhotoService(storage, userService, apartmentService);
    }

    @AfterEach
    void after() {
        mockedUserAwsPhoto.close();
        mockedApartmentAwsPhoto.close();
    }

    @Test
    void changeUserPhoto() {
        String email = "email";
        String photoLink = "photoLink";

        mockedUserAwsPhoto.when(
                        () -> UserAwsPhoto.getInstanceToUpload(email, photoFile))
                .thenReturn(userPhoto);
        when(storage.uploadPhoto(userPhoto))
                .thenReturn(photoLink);

        String actual = service.changeUserPhoto(email, photoFile);

        assertEquals(photoLink, actual);
        verify(userService, times(1))
                .changeUserPhoto(email, photoLink);
    }

    @Test
    void deleteUserPhoto() {
        String email = "email@";

        mockedUserAwsPhoto.when(
                        () -> UserAwsPhoto.getInstanceToDelete(email))
                .thenReturn(userPhoto);

        service.deleteUserPhoto(email);

        verify(storage, times(1))
                .deletePhoto(userPhoto);
        verify(userService, times(1))
                .deleteUserPhotoByEmail(email);
    }

    @Test
    void createApartmentPhoto() {
        Long id = 1L;
        String photoLink = "link://";

        when(apartmentService.getOne(id))
                .thenReturn(apartment);
        mockedApartmentAwsPhoto.when(
                        () -> ApartmentAwsPhoto.getInstanceToUpload(id, photoFile))
                .thenReturn(apartmentPhoto);
        when(storage.uploadPhoto(apartmentPhoto))
                .thenReturn(photoLink);

        var actual = service.createApartmentPhoto(id, photoFile);

        assertEquals(photoLink, actual);
        verify(apartment, times(1))
                .addPhoto(new Photo(photoLink));
    }

    @Test
    void deleteApartmentPhoto() {
        Long id = 1L;
        String photoLink = "link://filename";

        mockedApartmentAwsPhoto.when(
                        () -> ApartmentAwsPhoto.getInstanceToDelete(photoLink))
                .thenReturn(apartmentPhoto);

        service.deleteApartmentPhoto(id, photoLink);

        verify(apartmentService, times(1))
                .validateApartmentHasPhoto(id, photoLink);
        verify(storage, times(1))
                .deletePhoto(apartmentPhoto);
        verify(apartmentService, times(1))
                .deleteApartmentPhotoByLink(id, photoLink);
    }
}
