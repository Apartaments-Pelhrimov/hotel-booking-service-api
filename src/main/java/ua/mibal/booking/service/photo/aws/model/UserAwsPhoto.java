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
public class UserAwsPhoto extends AwsPhoto {
    private static final String folder = "users";

    protected UserAwsPhoto(String username, MultipartFile photo) {
        super(username, folder, photo);
    }

    protected UserAwsPhoto(String username) {
        super(username, folder);
    }

    public static UserAwsPhoto getInstanceToUpload(String username, MultipartFile photo) {
        return new UserAwsPhoto(username, photo);
    }

    public static UserAwsPhoto getInstanceToDelete(String username) {
        return new UserAwsPhoto(username);
    }
}
