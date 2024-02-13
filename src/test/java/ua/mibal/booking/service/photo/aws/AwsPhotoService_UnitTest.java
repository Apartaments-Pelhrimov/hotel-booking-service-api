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

import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.web.multipart.MultipartFile;
import ua.mibal.booking.model.entity.Apartment;
import ua.mibal.booking.model.entity.User;
import ua.mibal.booking.service.ApartmentService;
import ua.mibal.booking.service.UserService;
import ua.mibal.booking.service.photo.aws.components.AwsStorage;
import ua.mibal.booking.service.photo.aws.model.AwsPhoto;
import ua.mibal.booking.service.photo.aws.model.AwsPhotoResponse;
import ua.mibal.booking.service.photo.model.PhotoResponse;
import ua.mibal.booking.test.annotations.UnitTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.clearAllCaches;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
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
    private User user;
    @Mock
    private AwsPhoto photo;
    @Mock
    private AwsPhotoResponse awsPhotoResponse;

    private MockedStatic<AwsPhoto> mockedAwsPhoto;

    @BeforeEach
    void setup() {
        mockedAwsPhoto = mockStatic(AwsPhoto.class);
        service = new AwsPhotoService(storage, userService, apartmentService);
    }

    @AfterEach
    void after() {
        mockedAwsPhoto.close();
        clearAllCaches();
    }

    @Test
    void getUserPhoto() {
        String email = "email";
        String key = "photoKey";

        when(userService.getOne(email))
                .thenReturn(user);
        when(user.getPhotoKey())
                .thenReturn(key);
        when(storage.getPhotoBy(key))
                .thenReturn(awsPhotoResponse);

        PhotoResponse actual = service.getUserPhoto(email);

        assertEquals(awsPhotoResponse, actual);
    }


    @ParameterizedTest
    @InstancioSource
    void changeUserPhoto(User user, String email, String key) {
        when(userService.getOne(email))
                .thenReturn(user);
        mockedAwsPhoto.when(
                        () -> AwsPhoto.getInstanceToUpload(photoFile))
                .thenReturn(photo);
        when(photo.getKey())
                .thenReturn(key);

        String beforeKey = user.getPhotoKey();

        service.changeUserPhoto(email, photoFile);

        String afterKey = user.getPhotoKey();

        verify(storage, times(1))
                .uploadPhoto(photo);
        verify(storage, times(1))
                .deletePhotoBy(beforeKey);
        assertThat(afterKey, is(key));
    }

    @Test
    void deleteUserPhoto() {
        String email = "email@";
        String key = "photoKey";

        when(userService.getOne(email))
                .thenReturn(user);
        when(user.getPhotoKey())
                .thenReturn(key);

        service.deleteUserPhoto(email);

        verify(storage, times(1))
                .deletePhotoBy(key);
        verify(user, times(1))
                .deletePhoto();
    }

    @Test
    void getApartmentPhoto() {
        Long id = 1L;
        Integer photoIndex = 123;
        String key = "key";

        when(apartmentService.getOneFetchPhotos(id))
                .thenReturn(apartment);
        when(apartment.getPhotoKey(photoIndex))
                .thenReturn(key);
        when(storage.getPhotoBy(key))
                .thenReturn(awsPhotoResponse);

        PhotoResponse actual = service.getApartmentPhoto(id, photoIndex);

        assertEquals(awsPhotoResponse, actual);
    }

    @Test
    void createApartmentPhoto() {
        Long id = 1L;
        String key = "photoKey";

        when(apartmentService.getOne(id))
                .thenReturn(apartment);
        mockedAwsPhoto.when(
                        () -> AwsPhoto.getInstanceToUpload(photoFile))
                .thenReturn(photo);
        when(photo.getKey())
                .thenReturn(key);

        service.createApartmentPhoto(id, photoFile);

        verify(storage, times(1))
                .uploadPhoto(photo);
        verify(apartment, times(1))
                .addPhoto(key);
    }

    @Test
    void deleteApartmentPhoto() {
        Long id = 1L;
        Integer photoIndex = 123;
        String key = "key";

        when(apartmentService.getOneFetchPhotos(id))
                .thenReturn(apartment);
        when(apartment.getPhotoKey(photoIndex))
                .thenReturn(key);

        service.deleteApartmentPhoto(id, photoIndex);

        verify(storage, times(1))
                .deletePhotoBy(key);
        verify(apartment, times(1))
                .deletePhoto(key);
    }
}
