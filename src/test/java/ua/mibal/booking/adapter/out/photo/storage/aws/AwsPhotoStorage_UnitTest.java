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

package ua.mibal.booking.adapter.out.photo.storage.aws;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import ua.mibal.booking.adapter.out.photo.storage.aws.component.AwsRequestGenerator;
import ua.mibal.booking.adapter.out.photo.storage.aws.exception.AwsStorageException;
import ua.mibal.booking.adapter.out.photo.storage.aws.model.AwsPhoto;
import ua.mibal.test.annotation.UnitTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class AwsPhotoStorage_UnitTest {

    private AwsPhotoStorage storage;

    @Mock
    private S3Client s3Client;
    @Mock
    private AwsRequestGenerator requestGenerator;

    @Mock
    private MultipartFile file;
    @Mock
    private AwsPhoto photo;
    @Mock
    private PutObjectRequest putRequest;
    @Mock
    private DeleteObjectRequest deleteRequest;
    @Mock
    private RequestBody requestBody;

    private MockedStatic<RequestBody> mockedRequestBody;

    private MockedStatic<AwsPhoto> mockedAwsPhoto;


    @BeforeEach
    public void beforeEach() {
        storage = new AwsPhotoStorage(s3Client, requestGenerator);
        mockedRequestBody = Mockito.mockStatic(RequestBody.class);
        mockedAwsPhoto = Mockito.mockStatic(AwsPhoto.class);
    }

    @AfterEach
    public void closeMocks() {
        mockedRequestBody.close();
        mockedAwsPhoto.close();
    }

    @Test
    void uploadPhoto() throws IOException {
        String photoKey = "KEY";

        mockedAwsPhoto
                .when(() -> AwsPhoto.of(file))
                .thenReturn(photo);
        when(requestGenerator.generatePutRequest(photo))
                .thenReturn(putRequest);
        when(photo.getRequestBody())
                .thenReturn(requestBody);
        when(photo.getKey())
                .thenReturn(photoKey);

        String actualKey = storage.uploadPhoto(file);

        assertEquals(photoKey, actualKey);
        verify(s3Client, times(1))
                .putObject(putRequest, requestBody);
    }

    @Test
    void uploadPhoto_should_throw_AwsStorageException_if_SdkException_thrown() throws IOException {
        mockedAwsPhoto
                .when(() -> AwsPhoto.of(file))
                .thenReturn(photo);
        when(requestGenerator.generatePutRequest(photo))
                .thenReturn(putRequest);
        when(photo.getRequestBody())
                .thenReturn(requestBody);
        when(s3Client.putObject(putRequest, requestBody))
                .thenThrow(SdkException.class);

        assertThrows(AwsStorageException.class,
                () -> storage.uploadPhoto(file));
    }

    @Test
    void uploadPhoto_should_throw_AwsStorageException_if_IOException_thrown() throws IOException {
        mockedAwsPhoto
                .when(() -> AwsPhoto.of(file))
                .thenReturn(photo);
        when(requestGenerator.generatePutRequest(photo))
                .thenReturn(putRequest);
        when(photo.getRequestBody())
                .thenThrow(IOException.class);

        assertThrows(AwsStorageException.class,
                () -> storage.uploadPhoto(file));
    }

    @Test
    void deletePhoto() {
        String key = "key";

        when(requestGenerator.generateDeleteRequest(key))
                .thenReturn(deleteRequest);

        storage.deletePhotoBy(key);

        verify(s3Client, times(1))
                .deleteObject(deleteRequest);
    }

    @Test
    void deletePhoto_should_throw_AwsStorageException() {
        String key = "key";

        when(requestGenerator.generateDeleteRequest(key))
                .thenReturn(deleteRequest);
        when(s3Client.deleteObject(deleteRequest))
                .thenThrow(SdkException.class);

        assertThrows(AwsStorageException.class,
                () -> storage.deletePhotoBy(key));
    }
}
