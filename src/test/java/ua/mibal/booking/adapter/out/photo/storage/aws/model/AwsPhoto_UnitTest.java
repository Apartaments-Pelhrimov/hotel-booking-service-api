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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import ua.mibal.booking.adapter.out.photo.storage.aws.exception.IllegalPhotoFormatException;
import ua.mibal.test.annotation.UnitTest;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
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
    @Mock
    private RequestBody requestBody;

    private MockedStatic<RequestBody> mockedRequestBody;

    @BeforeEach
    void setupStaticMocks() {
        mockedRequestBody = mockStatic(RequestBody.class);
    }

    @AfterEach
    void closeStaticMocks() {
        mockedRequestBody.close();
    }

    @ParameterizedTest
    @CsvSource({"png", "jpg", "jpeg"})
    void constructor(String allowedExtension) {
        String legalFileName = "fileName." + allowedExtension;

        when(file.getOriginalFilename())
                .thenReturn(legalFileName);

        assertDoesNotThrow(
                () -> AwsPhoto.of(file));
    }

    @ParameterizedTest
    @CsvSource({"txt", "pdf", "svg", "''"})
    void constructor_should_throw_IllegalPhotoFormatException(String illegalExtension) {
        String illegalFileName = "fileName." + illegalExtension;

        when(file.getOriginalFilename())
                .thenReturn(illegalFileName);

        assertThrows(IllegalPhotoFormatException.class,
                () -> AwsPhoto.of(file));
    }

    @Test
    void getContentType() {
        when(file.getOriginalFilename())
                .thenReturn("image.jpg");
        String expectedContentType = "image/jpg";

        AwsPhoto photo = AwsPhoto.of(file);

        assertEquals(expectedContentType, photo.getContentType());
    }

    @Test
    void getRequestBody() throws IOException {
        byte[] fileBytes = "CONTENT".getBytes(UTF_8);

        when(file.getOriginalFilename())
                .thenReturn("legalFileName.jpg");
        when(file.getBytes())
                .thenReturn(fileBytes);
        mockedRequestBody
                .when(() -> RequestBody.fromBytes(fileBytes))
                .thenReturn(requestBody);

        RequestBody actualRequestBody = AwsPhoto.of(file).getRequestBody();

        assertEquals(requestBody, actualRequestBody);
    }

    @Test
    void getRequestBody_should_throw_IOException() throws IOException {
        when(file.getOriginalFilename())
                .thenReturn("legalFileName.jpg");
        when(file.getBytes())
                .thenThrow(IOException.class);

        assertThrows(IOException.class,
                () -> AwsPhoto.of(file).getRequestBody());
    }

    // todo key generating test
}
