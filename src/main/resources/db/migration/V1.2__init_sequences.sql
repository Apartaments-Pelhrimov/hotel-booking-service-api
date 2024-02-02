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

create sequence if not exists apartment_instances_seq
    increment by 50;

create sequence if not exists apartments_seq
    increment by 50;

create sequence if not exists comments_seq
    increment by 50;

create sequence if not exists hotel_turning_off_times_seq
    increment by 50;

create sequence if not exists reservations_seq
    increment by 50;

create sequence if not exists rooms_seq
    increment by 50;

create sequence if not exists users_seq
    increment by 50;
