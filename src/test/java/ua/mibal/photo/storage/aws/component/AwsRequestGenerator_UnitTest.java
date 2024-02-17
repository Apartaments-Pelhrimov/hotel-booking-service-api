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

package ua.mibal.photo.storage.aws.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ua.mibal.test.annotation.UnitTest;
import ua.mibal.photo.storage.aws.config.AwsProps.AwsBucketProps;
import ua.mibal.photo.storage.aws.model.AwsPhoto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class AwsRequestGenerator_UnitTest {

    private AwsRequestGenerator requestGenerator;

    @Mock
    private AwsBucketProps bucketProps;

    @Mock
    private AwsPhoto photo;

    private String bucketName = "bucket-name";

    @BeforeEach
    void setup() {
        when(bucketProps.name())
                .thenReturn(bucketName);
        requestGenerator = new AwsRequestGenerator(bucketProps);
    }

    @Test
    void generatePutRequest() {
        String photoKey = "photoKey";
        String contentType = "content-type";

        when(photo.getKey())
                .thenReturn(photoKey);
        when(photo.getContentType())
                .thenReturn(contentType);

        var actual = requestGenerator.generatePutRequest(photo);

        assertEquals(bucketName, actual.getValueForField("Bucket", String.class).get());
        assertEquals(photoKey, actual.getValueForField("Key", String.class).get());
        assertEquals(contentType, actual.getValueForField("ContentType", String.class).get());
    }

    @Test
    void generateDeleteRequest() {
        String key = "photoKey";

        var actual = requestGenerator.generateDeleteRequest(key);

        assertEquals(bucketName, actual.getValueForField("Bucket", String.class).get());
        assertEquals(key, actual.getValueForField("Key", String.class).get());
    }

    @Test
    void generateGetRequest() {
        String key = "photoKey";

        var actual = requestGenerator.generateGetRequest(key);

        assertEquals(bucketName, actual.getValueForField("Bucket", String.class).get());
        assertEquals(key, actual.getValueForField("Key", String.class).get());
    }
}
