drop table if exists `outbox_event`;
create table if not exists `outbox_event`
(
    id             varchar(255) primary key,
    aggregate_type varchar(255) not null COMMENT 'kafka topic name',
    aggregate_id   varchar(255) not null COMMENT 'add kafka header',
    command_type   varchar(255) not null COMMENT 'add kafka header',
    payload        longtext     not null,
    creation_time  datetime     not null
);

