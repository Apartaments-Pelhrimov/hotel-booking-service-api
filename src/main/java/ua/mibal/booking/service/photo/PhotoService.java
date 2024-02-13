/*
 * Copyright (c) 2023. Mykhailo Balakhon mailto:9mohapx9@gmail.com
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

package ua.mibal.booking.service.photo;

import org.springframework.web.multipart.MultipartFile;
import ua.mibal.booking.service.photo.model.PhotoResource;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public interface PhotoService {

    /**
     * Returns {@link PhotoResource} with user's profile photo
     *
     * @param email user's email used as user identifier
     * @return {@link PhotoResource} with user's profile photo
     */
    PhotoResource getUserPhoto(String email);

    /**
     * Deletes user's previous (old) photo from photo storage.
     * Deletes old photo identifier from user entity.
     * Uploads new {@link MultipartFile} into photo storage.
     * Adds new photo identifier to user entity.
     *
     * @param email user's email used as user identifier
     * @param photo photo to upload
     */
    void changeUserPhoto(String email, MultipartFile photo);

    /**
     * Method deletes user's photo from photo storage and
     * deletes previous (old) photo identifier from user entity.
     *
     * @param email user's email used as user identifier
     */
    void deleteUserPhoto(String email);

    /**
     * Returns {@link PhotoResource} with the apartment photo
     *
     * @param apartmentId identifier of the wanted apartment
     * @param photoIndex  index of the wanted apartment photo
     * @return {@link PhotoResource} with the apartment photo
     */
    PhotoResource getApartmentPhoto(Long apartmentId, Integer photoIndex);

    /**
     * Uploads new {@link MultipartFile} photo into photo storage.
     * Adds the new photo to the apartment entity.
     *
     * @param apartmentId identifier of the wanted apartment
     * @param photo       photo to upload
     */
    void createApartmentPhoto(Long apartmentId, MultipartFile photo);

    /**
     * Method deletes apartment's photo from photo storage and
     * deletes photo identifier from apartment entity.
     *
     * @param apartmentId identifier of the wanted apartment
     * @param photoIndex  index of the wanted apartment photo
     */
    void deleteApartmentPhoto(Long apartmentId, Integer photoIndex);
}
