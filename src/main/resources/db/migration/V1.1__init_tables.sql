create table if not exists apartments
(
    id             bigint       not null
        primary key,
    class          varchar(255) not null
        constraint apartments_apartment_class_check
            check ((class)::text = ANY (ARRAY [('COMFORT'::character varying)::text, ('STANDARD'::character varying)::text])),
    name           varchar(255) not null,
    bathroom       integer      not null
        constraint apartments_bathroom_check
            check (bathroom = ANY (ARRAY [0, 1])),
    kitchen        integer      not null
        constraint apartments_kitchen_check
            check (kitchen = ANY (ARRAY [0, 1])),
    meals_included integer      not null
        constraint apartments_meals_included_check
            check (meals_included = ANY (ARRAY [0, 1])),
    refrigerator   integer      not null
        constraint apartments_refrigerator_check
            check (refrigerator = ANY (ARRAY [0, 1])),
    wifi           integer      not null
        constraint apartments_wifi_check
            check (wifi = ANY (ARRAY [0, 1]))
);

create table if not exists apartment_instances
(
    id               bigint       not null
        primary key,
    booking_ical_url varchar(255),
    name             varchar(255) not null,
    apartment_id     bigint       not null
        constraint apartment_instances_apartment_id_fk
            references apartments
);

create index if not exists apartment_instances_apartment_id_idx
    on apartment_instances (apartment_id);

create table if not exists apartment_instances_turning_off_times
(
    apartment_instance_id bigint       not null
        constraint apartment_instances_turning_off_times_apartment_instance_id_fk
            references apartment_instances,
    "from"                timestamp(6) not null,
    "to"                  timestamp(6) not null
);

create index if not exists apartment_instances_turning_off_times_apartment_instance_id_idx
    on apartment_instances_turning_off_times (apartment_instance_id);

create table if not exists apartment_photos
(
    apartment_id bigint  not null
        constraint apartment_photos_apartment_id_fk
            references apartments,
    photo_link   varchar(255)
        constraint apartment_photos_photo_link_idx
            unique,
    photos_order integer not null,
    primary key (apartment_id, photos_order)
);

create table if not exists hotel_turning_off_times
(
    id     bigint       not null
        primary key,
    event  varchar(255),
    "from" timestamp(6) not null,
    "to"   timestamp(6) not null
);

create table if not exists prices
(
    apartment_id bigint         not null
        constraint prices_apartment_id_fk
            references apartments,
    price        numeric(38, 2) not null,
    person       integer        not null,
    constraint prices_apartment_id_and_person_uq
        unique (apartment_id, person)
);

create index if not exists prices_apartment_id_idx
    on prices (apartment_id);

create table if not exists rooms
(
    id           bigint       not null
        primary key,
    name         varchar(255) not null,
    type         varchar(255) not null
        constraint rooms_type_check
            check ((type)::text = ANY (ARRAY [('BEDROOM'::character varying)::text, ('LIVING_ROOM'::character varying)::text, ('MEETING_ROOM'::character varying)::text])),
    apartment_id bigint       not null
        constraint rooms_apartment_id_fk
            references apartments
);

create table if not exists beds
(
    room_id bigint       not null
        constraint beds_room_id_fk
            references rooms,
    size    integer      not null,
    type    varchar(255) not null
        constraint beds_type_check
            check ((type)::text = ANY (ARRAY [('BUNK'::character varying)::text, ('CONNECTED'::character varying)::text, ('TRANSFORMER'::character varying)::text]))
);

create table if not exists users
(
    id                   bigint       not null
        primary key,
    email                varchar(255) not null
        constraint uk_6dotkott2kjsp8vw4d0m25fb7
            unique
        constraint uk_sx468g52bpetvlad2j9y0lptc
            unique,
    enabled              integer      not null
        constraint users_enabled_check
            check (enabled = ANY (ARRAY [0, 1])),
    first_name           varchar(255) not null,
    last_name            varchar(255) not null,
    receive_news_emails  integer      not null
        constraint users_receive_news_emails_check
            check (receive_news_emails = ANY (ARRAY [0, 1])),
    receive_order_emails integer      not null
        constraint users_receive_order_emails_check
            check (receive_order_emails = ANY (ARRAY [0, 1])),
    password             varchar(255) not null,
    phone                varchar(255) not null,
    photo_link           varchar(255),
    role                 varchar(255) not null
        constraint users_role_check
            check ((role)::text = ANY (ARRAY [('USER'::character varying)::text, ('MANAGER'::character varying)::text]))
);

create table if not exists comments
(
    id           bigint           not null
        primary key,
    body         varchar(255)     not null,
    created_at   timestamp(6)     not null,
    rate         double precision not null,
    apartment_id bigint           not null
        constraint comments_apartment_id_fk
            references apartments,
    user_id      bigint           not null
        constraint comments_user_id_fk
            references users
);

create index if not exists comments_apartment_id_idx
    on comments (apartment_id);

create index if not exists comments_user_id_idx
    on comments (user_id);

create table if not exists reservations
(
    id                    bigint         not null
        primary key,
    created_at            timestamp(6)   not null,
    full_price            numeric(38, 2) not null,
    price                 numeric(38, 2) not null,
    person                integer        not null,
    "from"                timestamp(6)   not null,
    "to"                  timestamp(6)   not null,
    state                 varchar(255)   not null
        constraint reservations_state_check
            check ((state)::text = ANY (ARRAY [('PROCESSED'::character varying)::text, ('REJECTED'::character varying)::text])),
    apartment_instance_id bigint         not null
        constraint reservations_apartment_instance_id_fk
            references apartment_instances,
    user_id               bigint         not null
        constraint reservations_user_id_fk
            references users
);

create table if not exists reservation_rejections
(
    reservation_id   bigint       not null
        constraint reservation_rejections_reservation_id_fk
            references reservations,
    reason           varchar(255) not null,
    user_id          bigint       not null
        constraint reservation_rejections_user_id
            references users,
    rejections_order integer      not null,
    primary key (reservation_id, rejections_order)
);

create index if not exists reservation_rejections_reservation_id_idx
    on reservation_rejections (reservation_id);

create index if not exists reservations_apartment_instance_id_idx
    on reservations (apartment_instance_id);

create index if not exists reservations_user_id_idx
    on reservations (user_id);

create table if not exists tokens
(
    user_id    bigint       not null
        constraint activation_codes_pkey
            primary key
        constraint activation_codes_user_id_fk
            references users,
    value      varchar(511) not null
        constraint uk_orf4l3c7ed8xc967wbfvvn0ey
            unique
        constraint uk_l2k1uc5jsm6ymfli8ytg12736
            unique,
    expires_at timestamp(6) not null,
    created_at timestamp(6) not null
);

create index if not exists activation_codes_user_id_idx
    on tokens (user_id);

