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

package ua.mibal.booking.adapter.out.photo.storage.aws.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import ua.mibal.booking.adapter.out.photo.storage.aws.config.AwsProps;
import ua.mibal.booking.adapter.out.photo.storage.aws.model.AwsPhoto;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Component
public class AwsRequestGenerator {
    private final AwsProps.AwsBucketProps awsBucketProps;

    public PutObjectRequest generatePutRequest(AwsPhoto photo) {
        return PutObjectRequest.builder()
                .bucket(awsBucketProps.name())
                .key(photo.getKey())
                .contentType(photo.getContentType())
                .build();
    }

    public DeleteObjectRequest generateDeleteRequest(String key) {
        return DeleteObjectRequest.builder()
                .bucket(awsBucketProps.name())
                .key(key)
                .build();
    }

    public GetObjectRequest generateGetRequest(String key) {
        return GetObjectRequest.builder()
                .bucket(awsBucketProps.name())
                .key(key)
                .build();
    }
}
