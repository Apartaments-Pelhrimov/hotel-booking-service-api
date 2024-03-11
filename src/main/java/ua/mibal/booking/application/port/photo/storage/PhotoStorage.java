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

package ua.mibal.booking.application.port.photo.storage;

import org.springframework.web.multipart.MultipartFile;
import ua.mibal.booking.application.port.photo.storage.model.PhotoResource;

/**
 * The {@code PhotoStorage} interface defines the contract for a service responsible for
 * photo storage.
 * Implementations of this interface are expected to provide functionality for store
 * photos by unique keys.
 *
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public interface PhotoStorage {

    /**
     * Retrieves a {@code PhotoResource} based on the specified key.
     *
     * @param key The unique key associated with the desired photo.
     * @return A {@code PhotoResource} representing the requested photo.
     */
    PhotoResource getPhotoBy(String key);

    /**
     * Uploads a photo provided as a {@code MultipartFile} to the photo storage.
     *
     * @param photo The {@code MultipartFile} representing the photo to be uploaded.
     * @return A {@code String} representing the unique key assigned to the uploaded photo.
     */
    String uploadPhoto(MultipartFile photo);

    /**
     * Deletes a photo from storage based on the specified key.
     *
     * @param key The unique key associated with the photo to be deleted.
     */
    void deletePhotoBy(String key);
}
