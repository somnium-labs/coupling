drop table if exists `outbox_event`;
create table if not exists `outbox_event`
(
    id             bigint auto_increment primary key,
    aggregate_type varchar(255) not null COMMENT 'kafka topic name',
    aggregate_id   varchar(255) not null COMMENT 'add kafka header',
    headers        longtext     not null,
    payload        longtext     not null,
    creation_time  datetime     not null
);

