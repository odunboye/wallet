create table if not exists wallet
(
    id               uuid        not null
        primary key,
    account_number   varchar(14) unique,
    balance          numeric(18, 2) default 0.0,
    currency         varchar(5)       default 'NGN'::character varying,
    status           varchar(20)      default 'ACTIVE'::character varying,
    created          timestamp        default CURRENT_TIMESTAMP
);

create table if not exists wallet_transaction
(
    id                   uuid        not null
        primary key,
    account_number       varchar(14),
    amount               numeric(18, 2) default 0.0,
    signed_amount        numeric(18, 2) default 0.0,
    reference            varchar(255) unique,
    transaction_type     varchar(20) not null,
    balance_after        numeric(18, 2) default 0.0,
    balance_before       numeric(18, 2)   default 0.0,
    currency             varchar(5)       default 'NGN'::character varying,
    wallet_id   uuid
        constraint fkm2axb1q8lpufx7lj50vvbu561
            references wallet,
    created              timestamp        default CURRENT_TIMESTAMP
);

CREATE INDEX idx_wallet_transaction_account_number
ON wallet_transaction(account_number);

INSERT INTO wallet (id, account_number)
VALUES(gen_random_uuid(), '0001'), (gen_random_uuid(), '0002'), (gen_random_uuid(), '0003');