drop table if exists order_item;
drop table if exists orders;
create table orders (
    id bigint AUTO_INCREMENT not null primary key,
    user_id bigint
);
create table order_item (
    id bigint AUTO_INCREMENT not null primary key,
    order_id bigint,
    product_id bigint,
    quantity int,
    foreign key (order_id) references orders(id)
);