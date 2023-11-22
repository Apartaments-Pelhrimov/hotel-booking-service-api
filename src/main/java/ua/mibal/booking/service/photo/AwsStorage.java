/*
 * Copyright (c) 2023. Mykhailo Balakhon, mailto:9mohapx9@gmail.com
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

import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import ua.mibal.booking.config.properties.AwsProps;

import static software.amazon.awssdk.core.sync.RequestBody.fromBytes;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@Service
public class AwsStorage {
    private final S3Client s3Client;
    private final String bucketName;

    public AwsStorage(S3Client s3Client, AwsProps awsProps) {
        this.s3Client = s3Client;
        this.bucketName = awsProps.bucketName();
    }

    public String uploadImage(String folder, String name, byte[] image) {
        uploadPngImage(folder, name, image);
        return getLink(folder, name);
    }

    private void uploadPngImage(String folder, String name, byte[] file) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(folder + name)
                .contentType("image/png")
                .build();
        s3Client.putObject(putObjectRequest, fromBytes(file));
    }

    private String getLink(String folder, String name) {
        GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                .bucket(bucketName)
                .key(folder + name)
                .build();
        return s3Client.utilities()
                .getUrl(getUrlRequest)
                .toExternalForm();
    }

    public boolean delete(String folder, String name) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(folder + name)
                .build();
        try {
            s3Client.deleteObject(deleteObjectRequest);
            return true;
        } catch (SdkException e) {
            return false;
        }
    }
}
