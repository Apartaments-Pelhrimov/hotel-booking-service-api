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

package ua.mibal.booking.service.photo.storage.api.model;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;
import ua.mibal.booking.service.photo.storage.api.exception.IllegalPhotoFormatException;

import java.io.IOException;
import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static org.springframework.util.StringUtils.getFilenameExtension;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public abstract class Photo {

    private final MultipartFile file;
    @Getter
    private final String key;
    private final Extension extension;

    protected Photo(MultipartFile photo) {
        Objects.requireNonNull(photo);
        this.extension = getPhotoExtension(photo.getOriginalFilename());
        this.key = generateFileKey(photo);
        this.file = photo;
    }

    public String getContentType() {
        return "image/" + extension.getExtension();
    }

    public byte[] getPhoto() throws IOException {
        return file.getBytes();
    }

    protected abstract String generateFileKey(MultipartFile photo);

    private Extension getPhotoExtension(String photoName) {
        try {
            return getPhotoExtensionFrom(photoName);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalPhotoFormatException(photoName);
        }
    }

    private Extension getPhotoExtensionFrom(String photoName) {
        String extension = getFilenameExtension(photoName);
        requireNonNull(extension);
        return Extension.of(extension);
    }

    /**
     * @author Mykhailo Balakhon
     * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
     */
    public enum Extension {

        PNG, JPG, JPEG, NONE;

        public static Extension of(String extension) {
            return valueOf(extension.toUpperCase());
        }

        public String getExtension() {
            if (this == NONE) {
                throw new UnsupportedOperationException(
                        "Trying to call PhotoExtension.getExtension() " +
                        "on PhotoExtension.NONE instance");
            }
            return name().toLowerCase();
        }
    }
}
