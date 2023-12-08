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

package ua.mibal.booking.service.util;

import org.springframework.util.StringUtils;
import ua.mibal.booking.model.exception.IllegalPhotoFormatException;
import ua.mibal.booking.service.photo.PhotoExtension;

import java.util.Objects;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class FileNameUtils {

    public static PhotoExtension getPhotoExtension(String fileName) {
        String extension = null;
        try {
            extension = StringUtils.getFilenameExtension(fileName);
            return PhotoExtension.valueOf(Objects.requireNonNull(extension).toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalPhotoFormatException(extension);
        }
    }
}
