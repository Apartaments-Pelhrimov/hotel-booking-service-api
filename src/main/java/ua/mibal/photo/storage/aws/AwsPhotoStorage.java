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

package ua.mibal.photo.storage.aws;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import ua.mibal.photo.storage.api.PhotoStorage;
import ua.mibal.photo.storage.aws.component.AwsRequestGenerator;
import ua.mibal.photo.storage.aws.exception.AwsStorageException;
import ua.mibal.photo.storage.aws.model.AwsPhoto;
import ua.mibal.photo.storage.aws.model.AwsPhotoResource;

import java.io.IOException;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class AwsPhotoStorage implements PhotoStorage {
    private final S3Client s3Client;
    private final AwsRequestGenerator requestGenerator;

    @Override
    public AwsPhotoResource getPhotoBy(String key) {
        try {
            return getAwsPhotoBy(key);
        } catch (IOException e) {
            throw new AwsStorageException(
                    "Exception while getting Photo with " +
                    "key '%s'".formatted(key), e);
        }
    }

    @Override
    public String uploadPhoto(MultipartFile photo) {
        try {
            return uploadAwsPhoto(photo);
        } catch (IOException | SdkException e) {
            throw new AwsStorageException(
                    "Exception while uploading Photo with " +
                    "name '%s'".formatted(photo.getOriginalFilename()), e);
        }
    }

    @Override
    public void deletePhotoBy(String key) {
        try {
            deleteAwsPhotoBy(key);
        } catch (SdkException e) {
            throw new AwsStorageException(
                    "Exception while deleting Photo by " +
                    "key '%s'".formatted(key), e);
        }
    }

    private AwsPhotoResource getAwsPhotoBy(String key) throws IOException {
        GetObjectRequest getRequest = requestGenerator.generateGetRequest(key);
        var response = s3Client.getObject(getRequest);
        return AwsPhotoResource.of(
                response.readAllBytes(),
                response.response().contentType()
        );
    }

    private String uploadAwsPhoto(MultipartFile file) throws IOException {
        AwsPhoto photo = AwsPhoto.of(file);
        PutObjectRequest putRequest = requestGenerator.generatePutRequest(photo);
        s3Client.putObject(putRequest, photo.getRequestBody());
        return photo.getKey();
    }

    private void deleteAwsPhotoBy(String key) {
        DeleteObjectRequest deleteRequest = requestGenerator.generateDeleteRequest(key);
        s3Client.deleteObject(deleteRequest);
    }
}
