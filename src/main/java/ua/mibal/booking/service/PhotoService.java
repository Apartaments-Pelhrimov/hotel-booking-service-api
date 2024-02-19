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

package ua.mibal.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.User;
import ua.mibal.photo.storage.api.PhotoStorage;
import ua.mibal.photo.storage.api.component.PhotoFactory;
import ua.mibal.photo.storage.api.model.Photo;
import ua.mibal.photo.storage.api.model.PhotoResource;

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
    private final PhotoFactory photoFactory;

    public PhotoResource getUserPhoto(String email) {
        User user = userService.getOne(email);
        String photoKey = user.getPhotoKey();
        return storage.getPhotoBy(photoKey);
    }

    @Transactional
    public void changeUserPhoto(String email, MultipartFile photoFile) {
        User user = userService.getOne(email);
        String oldPhotoKey = user.getPhotoKey();
        storage.deletePhotoBy(oldPhotoKey);
        Photo newPhoto = photoFactory.getInstance(photoFile);
        storage.uploadPhoto(newPhoto);
        user.setPhoto(newPhoto.getKey());
    }

    @Transactional
    public void deleteUserPhoto(String email) {
        User user = userService.getOne(email);
        String photoKey = user.getPhotoKey();
        storage.deletePhotoBy(photoKey);
        user.deletePhoto();
    }

    public PhotoResource getApartmentPhoto(Long apartmentId, Integer photoIndex) {
        Apartment apartment = apartmentService.getOneFetchPhotos(apartmentId);
        String photoKey = apartment.getPhotoKey(photoIndex);
        return storage.getPhotoBy(photoKey);
    }

    @Transactional
    public void createApartmentPhoto(Long id, MultipartFile photoFile) {
        Apartment apartment = apartmentService.getOne(id);
        Photo photo = photoFactory.getInstance(photoFile);
        storage.uploadPhoto(photo);
        apartment.addPhoto(photo.getKey());
    }

    @Transactional
    public void deleteApartmentPhoto(Long apartmentId, Integer photoIndex) {
        Apartment apartment = apartmentService.getOneFetchPhotos(apartmentId);
        String photoKey = apartment.getPhotoKey(photoIndex);
        storage.deletePhotoBy(photoKey);
        apartment.deletePhoto(photoKey);
    }
}
