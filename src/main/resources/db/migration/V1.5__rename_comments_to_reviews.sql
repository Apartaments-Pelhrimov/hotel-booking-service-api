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


drop table public.comments;

create sequence public.reviews_seq
    increment by 50;

create table public.reviews
(
    id           bigint           not null
        primary key,
    body         varchar(255)     not null,
    created_at   timestamp(6)     not null,
    rate         double precision not null,
    apartment_id bigint           not null
        constraint reviews_apartment_id_fk
            references public.apartments,
    user_id      bigint           not null
        constraint reviews_user_id_fk
            references public.users
);

create index reviews_apartment_id_idx
    on public.reviews (apartment_id);

create index reviews_user_id_idx
    on public.reviews (user_id);
