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

package ua.mibal.booking.service.photo.aws.model;

import org.springframework.web.multipart.MultipartFile;
import ua.mibal.booking.model.exception.IllegalPhotoFormatException;

import java.io.IOException;
import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static org.springframework.util.StringUtils.getFilenameExtension;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class AwsPhoto {
    private final PhotoExtension extension;
    private final String key;
    private final MultipartFile file;

    public AwsPhoto(String name, String folder, MultipartFile file) {
        this.extension = getPhotoExtension(file.getOriginalFilename());
        this.key = generateFileKey(folder, name);
        this.file = file;
    }

    public AwsPhoto(String name, String folder) {
        this.extension = PhotoExtension.NONE;
        this.key = generateFileKey(folder, name);
        this.file = null;
    }

    public String getKey() {
        return key;
    }

    public String getContentType() {
        return "image/" + extension.getExtension();
    }

    public byte[] getPhoto() throws IOException {
        return file.getBytes();
    }

    private PhotoExtension getPhotoExtension(String photoName) {
        try {
            return getPhotoExtensionFrom(photoName);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalPhotoFormatException(photoName);
        }
    }

    private PhotoExtension getPhotoExtensionFrom(String photoName) {
        String photoExtension = getFilenameExtension(photoName);
        requireNonNull(photoExtension);
        return PhotoExtension.ofExtension(photoExtension);
    }

    private String generateFileKey(String folder, String name) {
        return "%s/%s".formatted(folder, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AwsPhoto photo = (AwsPhoto) o;

        if (extension != photo.extension) {
            return false;
        }
        if (!key.equals(photo.key)) {
            return false;
        }
        return Objects.equals(file, photo.file);
    }

    @Override
    public int hashCode() {
        int result = extension.hashCode();
        result = 31 * result + key.hashCode();
        result = 31 * result + (file != null ? file.hashCode() : 0);
        return result;
    }
}
