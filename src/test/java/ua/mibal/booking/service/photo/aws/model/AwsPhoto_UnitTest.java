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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.springframework.web.multipart.MultipartFile;
import ua.mibal.booking.model.exception.IllegalPhotoFormatException;
import ua.mibal.booking.test.annotations.UnitTest;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class AwsPhoto_UnitTest {

    private AwsPhoto uploadPhoto;

    private String name = "name";
    private String folder = "folder";
    private String fileName = "fileName.jpg";
    private byte[] fileBytes = "fileBytes".getBytes(UTF_8);

    @Mock
    private MultipartFile file;

    @BeforeEach
    void setup() throws IOException {
        when(file.getOriginalFilename())
                .thenReturn(fileName);
        when(file.getBytes())
                .thenReturn(fileBytes);

        uploadPhoto = new AwsPhoto(file);
    }

    @ParameterizedTest
    @CsvSource({"png", "jpg", "jpeg"})
    void constructor(String allowedExtension) {
        String legalFileName = "fileName." + allowedExtension;

        when(file.getOriginalFilename())
                .thenReturn(legalFileName);

        assertDoesNotThrow(
                () -> new AwsPhoto(file));
    }

    @ParameterizedTest
    @CsvSource({"txt", "pdf", "svg", "''"})
    void constructor_should_throw_IllegalPhotoFormatException(String illegalExtension) {
        String illegalFileName = "fileName." + illegalExtension;

        when(file.getOriginalFilename())
                .thenReturn(illegalFileName);

        assertThrows(IllegalPhotoFormatException.class,
                () -> new AwsPhoto(file));
    }

    @Test
    void getContentType() {
        when(file.getOriginalFilename())
                .thenReturn(fileName);

        String contentType = "image/jpg";

        assertEquals(contentType, uploadPhoto.getContentType());
    }

    @Test
    void getPhoto() throws IOException {
        assertEquals(fileBytes, uploadPhoto.getPhoto());
    }
}
