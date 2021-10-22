drop table if exists `customer_credit_reservation`;
drop table if exists `customer`;

create table if not exists `customer`
(
    id      bigint auto_increment primary key,
    amount  decimal(19, 2) null,
    name    varchar(255)   null,
    version bigint         null
);

create table if not exists `customer_credit_reservation`
(
    id          bigint auto_increment primary key,
    customer_id bigint         not null,
    amount      decimal(19, 2) null,

    constraint foreign key (customer_id) references customer (id)
);

