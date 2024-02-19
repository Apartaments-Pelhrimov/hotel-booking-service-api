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

package ua.mibal.photo.storage.aws.model;

import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import ua.mibal.photo.storage.api.exception.IllegalPhotoFormatException;
import ua.mibal.photo.storage.api.model.PhotoExtension;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static java.util.Objects.requireNonNull;
import static org.springframework.util.StringUtils.getFilenameExtension;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class AwsPhoto {
    private final String key;
    private final PhotoExtension extension;
    private final MultipartFile file;

    private AwsPhoto(MultipartFile file) {
        Objects.requireNonNull(file);
        this.extension = getPhotoExtension(file.getOriginalFilename());
        this.key = generateFileKey(file);
        this.file = file;
    }

    public static AwsPhoto of(MultipartFile file) {
        return new AwsPhoto(file);
    }

    public String getKey() {
        return this.key;
    }

    public String getContentType() {
        return "image/" + extension.getExtension();
    }

    public RequestBody getRequestBody() throws IOException {
        return RequestBody.fromBytes(file.getBytes());
    }

    private PhotoExtension getPhotoExtension(String photoName) {
        try {
            return getPhotoExtensionFrom(photoName);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalPhotoFormatException(photoName);
        }
    }

    private PhotoExtension getPhotoExtensionFrom(String photoName) {
        String extension = getFilenameExtension(photoName);
        requireNonNull(extension);
        return PhotoExtension.of(extension);
    }

    private String generateFileKey(MultipartFile photo) {
        String name = Optional.ofNullable(photo.getOriginalFilename())
                .orElseGet(photo::getName);
        return "" + now().hashCode() + name.hashCode();
    }
}
