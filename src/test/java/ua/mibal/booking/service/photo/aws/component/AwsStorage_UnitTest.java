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

package ua.mibal.booking.service.photo.aws.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import ua.mibal.booking.model.exception.service.AwsStorageException;
import ua.mibal.booking.service.photo.aws.components.AwsRequestGenerator;
import ua.mibal.booking.service.photo.aws.components.AwsStorage;
import ua.mibal.booking.service.photo.aws.model.AwsPhoto;

import java.io.IOException;
import java.net.URL;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AwsStorage_UnitTest {

    private AwsStorage storage;

    @Mock
    private S3Client s3Client;
    @Mock
    private AwsRequestGenerator requestGenerator;

    @Mock
    private AwsPhoto photo;
    @Mock
    private PutObjectRequest putRequest;
    @Mock
    private DeleteObjectRequest deleteRequest;
    @Mock
    private GetUrlRequest getUrlRequest;
    @Mock
    private RequestBody requestBody;
    @Mock
    private S3Utilities s3Utilities;
    @Mock
    private URL url;

    @BeforeEach
    public void beforeEach() {
        storage = new AwsStorage(s3Client, requestGenerator);
    }

    @Test
    void uploadPhoto() throws IOException {
        try (MockedStatic<RequestBody> mockedRequestBody = Mockito.mockStatic(RequestBody.class)) {
            uploadPhoto_mocked_RequestBody(mockedRequestBody);
        }
    }

    void uploadPhoto_mocked_RequestBody(MockedStatic<RequestBody> mockedRequestBody) throws IOException {
        byte[] photoBytes = "BYTES".getBytes(UTF_8);
        String photoLink = "test photo link";

        when(requestGenerator.generatePutRequest(photo))
                .thenReturn(putRequest);
        when(photo.getPhoto())
                .thenReturn(photoBytes);
        mockedRequestBody.when(() -> RequestBody.fromBytes(photoBytes))
                .thenReturn(requestBody);
        when(RequestBody.fromBytes(photoBytes))
                .thenReturn(requestBody);

        when(requestGenerator.generateGetUrlRequest(photo))
                .thenReturn(getUrlRequest);
        when(s3Client.utilities())
                .thenReturn(s3Utilities);
        when(s3Utilities.getUrl(getUrlRequest))
                .thenReturn(url);
        when(url.toExternalForm())
                .thenReturn(photoLink);

        var actual = storage.uploadPhoto(photo);

        verify(s3Client, times(1))
                .putObject(putRequest, requestBody);
        assertEquals(photoLink, actual);
    }

    @Test
    void uploadPhoto_should_throw_AwsStorageException() throws IOException {
        try (MockedStatic<RequestBody> mockedRequestBody = Mockito.mockStatic(RequestBody.class)) {
            uploadPhoto_should_throw_AwsStorageException_mocked_RequestBody(mockedRequestBody);
        }
    }

    private void uploadPhoto_should_throw_AwsStorageException_mocked_RequestBody(MockedStatic<RequestBody> mockedRequestBody) throws IOException {
        byte[] photoBytes = "BYTES".getBytes(UTF_8);

        when(requestGenerator.generatePutRequest(photo))
                .thenReturn(putRequest);
        when(photo.getPhoto())
                .thenReturn(photoBytes);
        mockedRequestBody.when(() -> RequestBody.fromBytes(photoBytes))
                .thenReturn(requestBody);
        when(RequestBody.fromBytes(photoBytes))
                .thenReturn(requestBody);

        when(s3Client.putObject(putRequest, requestBody))
                .thenThrow(SdkException.class);

        assertThrows(AwsStorageException.class,
                () -> storage.uploadPhoto(photo));
    }

    @Test
    void deletePhoto() {
        when(requestGenerator.generateDeleteRequest(photo))
                .thenReturn(deleteRequest);

        storage.deletePhoto(photo);

        verify(s3Client, times(1))
                .deleteObject(deleteRequest);
    }

    @Test
    void deletePhoto_should_throw_AwsStorageException() {
        when(requestGenerator.generateDeleteRequest(photo))
                .thenReturn(deleteRequest);
        when(s3Client.deleteObject(deleteRequest))
                .thenThrow(SdkException.class);

        assertThrows(AwsStorageException.class,
                () -> storage.deletePhoto(photo));
    }
}