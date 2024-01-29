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

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class ApartmentAwsPhoto extends AwsPhoto {
    private static final String folder = "apartments";

    protected ApartmentAwsPhoto(Long id, MultipartFile photo) {
        super(generatePhotoName(id, photo), folder, photo);
    }

    protected ApartmentAwsPhoto(String link) {
        super(getFileName(link), folder);
    }

    public static ApartmentAwsPhoto getInstanceToUpload(Long id, MultipartFile photo) {
        return new ApartmentAwsPhoto(id, photo);
    }

    public static ApartmentAwsPhoto getInstanceToDelete(String link) {
        return new ApartmentAwsPhoto(link);
    }

    private static String generatePhotoName(Long id, MultipartFile photo) {
        return id + "_" + photo.hashCode() + "_" + photo.getOriginalFilename();
    }

    private static String getFileName(String link) {
        String[] path = link.split("/");
        return path[path.length - 1];
    }
}
