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

package ua.mibal.booking.adapter.out.photo.storage.aws.model;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import ua.mibal.test.annotation.UnitTest;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.MediaType.IMAGE_PNG;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class AwsPhotoResource_UnitTest {

    @Test
    void of() {
        byte[] bytes = "PHOTO_CONTENT".getBytes(UTF_8);
        MediaType contentType = IMAGE_PNG;

        AwsPhotoResource response =
                AwsPhotoResource.of(bytes, IMAGE_PNG_VALUE);

        assertEquals(bytes, response.getBytes());
        assertEquals(contentType, response.getContentType());
    }

    @Test
    void of_should_throw_if_contentType_has_illegal_format() {
        byte[] bytes = "PHOTO_CONTENT".getBytes(UTF_8);
        String illegalContentType = "illegal-type";

        assertThrows(RuntimeException.class,
                () -> AwsPhotoResource.of(bytes, illegalContentType));
    }
}
