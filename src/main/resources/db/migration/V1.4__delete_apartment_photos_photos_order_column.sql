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

alter table apartment_photos
    drop constraint if exists apartment_photos_pkey;

alter table apartment_photos
    drop constraint if exists apartment_photos_aws_photo_key_idx;

alter table apartment_photos
    drop
        column if exists photos_order;

alter table if exists apartment_photos
    drop constraint if exists apartment_photos_aws_photo_key_idx;

alter table if exists apartment_photos
    add constraint apartment_photos_aws_photo_key_idx unique (aws_photo_key);
