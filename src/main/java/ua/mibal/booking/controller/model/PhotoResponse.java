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

package ua.mibal.booking.controller.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import ua.mibal.booking.service.photo.model.PhotoResource;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class PhotoResponse extends ResponseEntity<byte[]> {

    protected PhotoResponse(byte[] body, MediaType mediaType) {
        super(body, HttpStatus.OK);
        getHeaders().setContentType(mediaType);
    }

    public static PhotoResponse of(PhotoResource photo) {
        return new PhotoResponse(photo.getBytes(), photo.getContentType());
    }
}
