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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ua.mibal.booking.application.exception.ApartmentDoesNotHavePhotoException;
import ua.mibal.booking.application.port.photo.storage.PhotoStorage;
import ua.mibal.booking.application.port.photo.storage.model.PhotoResource;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.Photo;
import ua.mibal.booking.domain.User;
import ua.mibal.booking.domain.id.ApartmentId;

import java.util.Optional;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class PhotoService {
    private final PhotoStorage storage;
    private final UserService userService;
    private final ApartmentService apartmentService;

    public PhotoResource getPhotoBy(String key) {
        return storage.getPhotoBy(key);
    }

    @Transactional
    public void changeUserPhoto(String email, MultipartFile photo) {
        User user = userService.getOne(email);
        Optional<Photo> oldPhoto = user.getPhoto();
        if (oldPhoto.isPresent()) {
            storage.deletePhotoBy(oldPhoto.get().getKey());
        }
        String newPhotoKey = storage.uploadPhoto(photo);
        user.setPhoto(newPhotoKey);
    }

    @Transactional
    public void deleteUserPhoto(String email) {
        User user = userService.getOne(email);
        Optional<Photo> photo = user.getPhoto();
        if (photo.isEmpty()) {
            return;
        }
        storage.deletePhotoBy(photo.get().getKey());
        user.deletePhoto();
    }

    @Transactional
    public void createApartmentPhoto(ApartmentId apartmentId, MultipartFile photo) {
        Apartment apartment = apartmentService.getOneFetchPhotos(apartmentId);
        String newPhotoKey = storage.uploadPhoto(photo);
        apartment.addPhoto(newPhotoKey);
    }

    @Transactional
    public void deleteApartmentPhoto(ApartmentId apartmentId, String key) {
        Apartment apartment = apartmentService.getOneFetchPhotos(apartmentId);
        validateApartmentHasPhoto(apartment, key);
        storage.deletePhotoBy(key);
        apartment.deletePhoto(key);
    }

    private void validateApartmentHasPhoto(Apartment apartment, String key) {
        if (!apartment.hasPhoto(key)) {
            throw new ApartmentDoesNotHavePhotoException();
        }
    }
}
