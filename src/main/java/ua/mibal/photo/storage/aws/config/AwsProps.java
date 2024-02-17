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

package ua.mibal.photo.storage.aws.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import software.amazon.awssdk.regions.Region;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Validated
@ConfigurationProperties(prefix = "aws")
public record AwsProps(
        @NotNull
        @NotBlank
        String accessKeyId,
        @NotNull
        @NotBlank
        String secretAccessKey,
        @NotNull
        Region region,
        @NotNull
        AwsBucketProps bucket
) {
    @Validated
    @ConfigurationProperties(prefix = "aws.bucket")
    public record AwsBucketProps(
            @NotNull
            @NotBlank
            String name
    ) {
    }
}
