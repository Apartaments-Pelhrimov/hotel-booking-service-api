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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.multipart.MultipartFile;
import ua.mibal.booking.model.entity.Apartment;
import ua.mibal.booking.model.entity.embeddable.Photo;
import ua.mibal.booking.model.exception.IllegalPhotoFormatException;
import ua.mibal.booking.service.ApartmentService;
import ua.mibal.booking.service.UserService;
import ua.mibal.booking.service.photo.aws.components.AwsStorage;
import ua.mibal.booking.service.photo.aws.model.ApartmentAwsPhoto;
import ua.mibal.booking.service.photo.aws.model.AwsPhoto;
import ua.mibal.booking.service.photo.aws.model.UserAwsPhoto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @BeforeEach
    void setup() {
        service = new AwsPhotoService(storage, userService, apartmentService);
    }

    @ParameterizedTest
    @CsvSource({"png", "jpg", "jpeg"})
    void changeUserPhoto(String allowedExtension) {
        String email = "email";
        String fileName = "fileName." + allowedExtension;
        String photoLink = "photoLink";

        when(photoFile.getOriginalFilename())
                .thenReturn(fileName);
        AwsPhoto photo = new UserAwsPhoto(email, photoFile);
        when(storage.uploadPhoto(photo))
                .thenReturn(photoLink);

        String actual = service.changeUserPhoto(email, photoFile);

        assertEquals(photoLink, actual);
        verify(userService, times(1))
                .changeUserPhoto(email, photoLink);
    }

    @ParameterizedTest
    @CsvSource({"txt", "pdf", "svg", "''"})
    void changeUserPhoto_should_throw_IllegalPhotoFormatException(String illegalExtension) {
        String email = "email";
        String fileName = "fileName." + illegalExtension;

        when(photoFile.getOriginalFilename())
                .thenReturn(fileName);

        assertThrows(IllegalPhotoFormatException.class,
                () -> service.changeUserPhoto(email, photoFile));
    }

    @Test
    void deleteUserPhoto() {
        String email = "email@";
        AwsPhoto photo = new UserAwsPhoto(email);

        service.deleteUserPhoto(email);

        verify(storage, times(1))
                .deletePhoto(photo);
        verify(userService, times(1))
                .deleteUserPhotoByEmail(email);
    }

    @ParameterizedTest
    @CsvSource({"png", "jpg", "jpeg"})
    void createApartmentPhoto(String allowedExtension) {
        Long id = 1L;
        String fileName = "fileName." + allowedExtension;
        String photoLink = "link://";

        when(apartmentService.getOne(id))
                .thenReturn(apartment);
        when(photoFile.getOriginalFilename())
                .thenReturn(fileName);
        AwsPhoto photo = new ApartmentAwsPhoto(id, photoFile);
        when(storage.uploadPhoto(photo))
                .thenReturn(photoLink);

        var actual = service.createApartmentPhoto(id, photoFile);

        assertEquals(photoLink, actual);
        verify(apartment, times(1))
                .addPhoto(new Photo(photoLink));
    }

    @ParameterizedTest
    @CsvSource({"txt", "pdf", "svg", "''"})
    void createApartmentPhoto_should_throw_IllegalPhotoFormatException(String illegalExtension) {
        Long id = 1L;
        String fileName = "fileName." + illegalExtension;

        when(apartmentService.getOne(id))
                .thenReturn(apartment);
        when(photoFile.getOriginalFilename())
                .thenReturn(fileName);

        assertThrows(IllegalPhotoFormatException.class,
                () -> service.createApartmentPhoto(id, photoFile));
    }

    @Test
    void deleteApartmentPhoto() {
        Long id = 1L;
        String photoLink = "link://filename";
        AwsPhoto photo = new ApartmentAwsPhoto(photoLink);

        service.deleteApartmentPhoto(id, photoLink);

        verify(apartmentService, times(1))
                .validateApartmentHasPhoto(id, photoLink);
        verify(storage, times(1))
                .deletePhoto(photo);
        verify(apartmentService, times(1))
                .deleteApartmentPhotoByLink(id, photoLink);
    }
}
