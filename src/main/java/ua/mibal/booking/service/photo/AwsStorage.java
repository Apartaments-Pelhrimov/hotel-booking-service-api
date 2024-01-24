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

package ua.mibal.booking.service.photo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import ua.mibal.booking.config.properties.AwsProps.AwsBucketProps;
import ua.mibal.booking.model.exception.service.AwsStorageException;
import ua.mibal.booking.service.photo.aws.AwsPhoto;

import java.io.IOException;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class AwsStorage {
    private final S3Client s3Client;
    private final AwsBucketProps awsBucketProps;

    public void deletePhoto(AwsPhoto photo) {
        try {
            deleteAwsPhoto(photo);
        } catch (SdkException e) {
            throw new AwsStorageException(
                    "Exception while deleting Photo by " +
                    "key '%s'".formatted(photo.getKey()), e);
        }
    }

    public String uploadPhoto(AwsPhoto photo) {
        try {
            uploadAwsPhoto(photo);
            return getLink(photo);
        } catch (IOException | SdkException e) {
            throw new AwsStorageException(
                    "Exception while uploading Photo with " +
                    "key '%s'".formatted(photo.getKey()), e);
        }
    }

    private void deleteAwsPhoto(AwsPhoto photo) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(awsBucketProps.name())
                .key(photo.getKey())
                .build();
        s3Client.deleteObject(deleteRequest);
    }

    private void uploadAwsPhoto(AwsPhoto photo) throws IOException {
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(awsBucketProps.name())
                .key(photo.getKey())
                .contentType(photo.getContentType())
                .build();
        RequestBody requestBody = RequestBody.fromBytes(photo.getPhoto());
        s3Client.putObject(putRequest, requestBody);
    }

    private String getLink(AwsPhoto photo) {
        GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                .bucket(awsBucketProps.name())
                .key(photo.getKey())
                .build();
        return s3Client.utilities()
                .getUrl(getUrlRequest)
                .toExternalForm();
    }
}
