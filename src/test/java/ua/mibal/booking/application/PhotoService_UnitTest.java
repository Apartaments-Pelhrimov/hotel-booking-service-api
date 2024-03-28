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

import org.assertj.core.api.Assertions;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.springframework.web.multipart.MultipartFile;
import ua.mibal.booking.application.exception.ApartmentDoesNotHavePhotoException;
import ua.mibal.booking.application.port.photo.storage.PhotoStorage;
import ua.mibal.booking.application.port.photo.storage.model.PhotoResource;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.Photo;
import ua.mibal.booking.domain.User;
import ua.mibal.booking.domain.id.ApartmentId;
import ua.mibal.test.annotation.UnitTest;

import static java.util.Optional.empty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
@UnitTest
class PhotoService_UnitTest {

    private PhotoService service;

    @Mock
    private PhotoStorage storage;
    @Mock
    private UserService userService;
    @Mock
    private ApartmentService apartmentService;

    @Mock
    private MultipartFile photoFile;
    @Mock
    private Apartment apartment;
    @Mock
    private PhotoResource photoResource;

    @BeforeEach
    void setup() {
        service = new PhotoService(storage, userService, apartmentService);
    }

    @ParameterizedTest
    @InstancioSource
    void getPhotoBy(String key) {
        when(storage.getPhotoBy(key))
                .thenReturn(photoResource);

        PhotoResource actual = service.getPhotoBy(key);

        assertEquals(photoResource, actual);
    }


    @ParameterizedTest
    @InstancioSource
    void changeUserPhoto_should_change(User user, String email, String oldKey, String newKey) {
        user.setPhoto(oldKey);

        when(userService.getOne(email))
                .thenReturn(user);
        when(storage.uploadPhoto(photoFile))
                .thenReturn(newKey);

        service.changeUserPhoto(email, photoFile);

        verify(storage, times(1))
                .deletePhotoBy(oldKey);
        assertThat(user.getPhoto().get().getKey(), is(newKey));
    }

    @ParameterizedTest
    @InstancioSource
    void changeUserPhoto_should_create_if_User_has_no_photo(String email, String key) {
        User user = new User();

        when(userService.getOne(email))
                .thenReturn(user);
        when(storage.uploadPhoto(photoFile))
                .thenReturn(key);

        service.changeUserPhoto(email, photoFile);

        verify(storage, never())
                .deletePhotoBy(any());
        assertThat(user.getPhoto().get().getKey(), is(key));
    }

    @ParameterizedTest
    @InstancioSource
    void deleteUserPhoto_should_delete(User user, String email, String key) {
        user.setPhoto(key);

        when(userService.getOne(email))
                .thenReturn(user);

        service.deleteUserPhoto(email);

        verify(storage, times(1))
                .deletePhotoBy(key);
        assertEquals(empty(), user.getPhoto());
    }

    @ParameterizedTest
    @InstancioSource
    void deleteUserPhoto_should_ignore_if_User_has_no_photo(String email) {
        when(userService.getOne(email))
                .thenReturn(new User());

        service.deleteUserPhoto(email);

        verifyNoInteractions(storage);
    }

    @ParameterizedTest
    @InstancioSource
    void createApartmentPhoto(Apartment apartment, ApartmentId id, String key) {
        when(apartmentService.getOneFetchPhotos(id))
                .thenReturn(apartment);
        when(storage.uploadPhoto(photoFile))
                .thenReturn(key);

        service.createApartmentPhoto(id, photoFile);

        Assertions.assertThat(apartment.getPhotos()).contains(new Photo(key));
    }

    @ParameterizedTest
    @InstancioSource
    void deleteApartmentPhoto(ApartmentId id, String key) {
        when(apartmentService.getOneFetchPhotos(id))
                .thenReturn(apartment);
        when(apartment.hasPhoto(key))
                .thenReturn(true);

        service.deleteApartmentPhoto(id, key);

        verify(storage, times(1))
                .deletePhotoBy(key);
        verify(apartment, times(1))
                .deletePhoto(key);
    }

    @ParameterizedTest
    @InstancioSource
    void deleteApartmentPhoto_should_throw_ApartmentDoesNotHavePhotoException(ApartmentId id, String key) {
        when(apartmentService.getOneFetchPhotos(id))
                .thenReturn(apartment);
        when(apartment.hasPhoto(key))
                .thenReturn(false);

        assertThrows(ApartmentDoesNotHavePhotoException.class,
                () -> service.deleteApartmentPhoto(id, key));

        verifyNoInteractions(storage);
    }
}
