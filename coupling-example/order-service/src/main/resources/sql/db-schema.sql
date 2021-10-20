drop table if exists `orders`;
create table if not exists `orders`
(
    id               bigint auto_increment primary key,
    customer_id      bigint         not null,
    amount           decimal(19, 2) null,
    rejection_reason varchar(255)   null,
    state            varchar(255)   null
);