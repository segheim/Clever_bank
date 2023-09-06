create table if not exists accounts
(
    id       bigserial,
    login    varchar(60) not null,
    password varchar(60) not null
);

alter table accounts
    owner to postgres;

create table if not exists bank_accounts
(
    id          bigserial,
    account_id  bigint                              not null,
    balance     numeric                             not null,
    date_create timestamp default CURRENT_TIMESTAMP not null
);

alter table bank_accounts
    owner to postgres;

create table if not exists transactions
(
    id                   bigserial,
    bank_account_id_from bigint                              not null,
    bank_account_id_to   bigserial,
    sum                  numeric                             not null,
    date_create          timestamp default CURRENT_TIMESTAMP not null,
    type                 varchar(40)                         not null
);

alter table transactions
    owner to postgres;

create table if not exists banks_bank_accounts
(
    bank_id         bigint not null,
    bank_account_id bigint not null
);

alter table banks_bank_accounts
    owner to postgres;

create table if not exists banks
(
    id   bigserial,
    name varchar(60) not null
);

alter table banks
    owner to postgres;


