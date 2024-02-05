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
    rename column photo_link to aws_photo_key;

alter index apartment_photos_photo_link_idx rename to apartment_photos_aws_photo_key_idx;

alter table users
    rename column photo_link to aws_photo_key;

create index users_aws_photo_key_idx
    on users (aws_photo_key);
