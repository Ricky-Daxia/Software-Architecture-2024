drop table if exists delivery_info;
create table delivery_info (
    id bigint AUTO_INCREMENT not null primary key,
    user_id bigint,
    items json
);