create table authorities
(
    id   bigint auto_increment
        primary key,
    name varchar(50) not null,
    constraint UKnb3atvjf9ov5d0egnuk47o5e
        unique (name)
);

create table tags
(
    id         binary(16)                               not null
        primary key,
    created_at datetime(3) default CURRENT_TIMESTAMP(3) not null,
    deleted_at datetime(3)                              null,
    slug       varchar(32)                              not null,
    constraint UKsn0d91hxu700qcw0n4pebp5vc
        unique (slug)
);

create table users
(
    id            binary(16)                               not null
        primary key,
    created_at    datetime(3) default CURRENT_TIMESTAMP(3) not null,
    deleted_at    datetime(3)                              null,
    email         varchar(200)                             not null,
    name          varchar(100)                             not null,
    nickname      varchar(30)                              null,
    profile_image varchar(255)                             null,
    social_id     varchar(255)                             not null,
    constraint UK2ty1xmrrgtn89xt7kyxx6ta7h
        unique (nickname),
    constraint UKg6t6xceecpep2b40vy35x1nve
        unique (social_id)
);

create table packages
(
    id         binary(16)                               not null
        primary key,
    created_at datetime(3) default CURRENT_TIMESTAMP(3) not null,
    deleted_at datetime(3)                              null,
    name       varchar(32)                              not null,
    user_id    binary(16)                               not null,
    constraint UKpsrex7rv4fnsmd9y7wj48gp4g
        unique (name, user_id),
    constraint FKcj3syi9lcukrlsh6o4llk0t8f
        foreign key (user_id) references users (id)
);

create table archives
(
    id             binary(16)                               not null
        primary key,
    created_at     datetime(3) default CURRENT_TIMESTAMP(3) not null,
    deleted_at     datetime(3)                              null,
    is_public      bit                                      not null,
    thumbnail_path varchar(255)                             null,
    title          varchar(64)                              not null,
    group_id       binary(16)                               not null,
    writer_id      binary(16)                               not null,
    is_replicable  bit                                      not null,
    type           enum ('NOTE', 'REFERENCE')               not null,
    summary        varchar(100)                             not null,
    constraint FKhlusw7pqmuxytatq5el2s1rp0
        foreign key (writer_id) references users (id),
    constraint FKshyq60k9iv2u67gyd7ft73c1s
        foreign key (group_id) references packages (id)
);

create table archive_blocks
(
    id         bigint auto_increment
        primary key,
    created_at datetime(3) default CURRENT_TIMESTAMP(3)              not null,
    deleted_at datetime(3)                                           null,
    position   int                                                   not null,
    type       enum ('H1', 'H2', 'H3', 'IMAGE', 'LINK', 'PARAGRAPH') not null,
    updated_at datetime(3) default CURRENT_TIMESTAMP(3)              not null on update CURRENT_TIMESTAMP(3),
    path       varchar(255)                                          null,
    url        varchar(255)                                          null,
    content    text                                                  null,
    archive_id binary(16)                                            null,
    constraint FKjlludoou1c4kv4ihgu470s7ff
        foreign key (archive_id) references archives (id),
    check (((`type` in ('PARAGRAPH','H1','H2','H3')) and (`content` is not null)) or ((`type` = 'IMAGE') and (`path` is not null)) or ((`type` = 'LINK') and (`url` is not null)))
);

create table archive_tag
(
    archive_id binary(16) not null,
    tag_id     binary(16) not null,
    primary key (archive_id, tag_id),
    constraint FKfy1420f2mocrqqe6v1wqadppv
        foreign key (tag_id) references tags (id),
    constraint FKkud0p3mq9hqamcy0jf9cabaop
        foreign key (archive_id) references archives (id)
);

create table bookmarks
(
    id         bigint auto_increment
        primary key,
    created_at datetime(3) default CURRENT_TIMESTAMP(3) not null,
    deleted_at datetime(3)                              null,
    is_marked  bit                                      not null,
    archive_id binary(16)                               not null,
    user_id    binary(16)                               not null,
    constraint FKdbsho2e05w5r13fkjqfjmge5f
        foreign key (user_id) references users (id),
    constraint FKi8rcqx1tmt8h2mabkfgnwhlwe
        foreign key (archive_id) references archives (id)
);

create table user_authority
(
    user_id      binary(16) not null,
    authority_id bigint     not null,
    primary key (user_id, authority_id),
    constraint FKhi46vu7680y1hwvmnnuh4cybx
        foreign key (user_id) references users (id),
    constraint FKil6f39w6fgqh4gk855pstsnmy
        foreign key (authority_id) references authorities (id)
);

