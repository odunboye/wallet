CREATE TABLE
IF NOT EXISTS public.customer
(
    customer_id uuid NOT NULL DEFAULT uuid_generate_v4
(),
    created timestamp
(6) without time zone,
    email character varying
(255) COLLATE pg_catalog."default",
    firstname character varying
(255) COLLATE pg_catalog."default",
    lastname character varying
(255) COLLATE pg_catalog."default",
    updated timestamp
(6) without time zone,
    first_name character varying
(255) COLLATE pg_catalog."default",
    last_name character varying
(255) COLLATE pg_catalog."default",
    CONSTRAINT customer_pkey PRIMARY KEY
(customer_id)
)


CREATE TABLE
IF NOT EXISTS public.wallet
(
    id uuid NOT NULL,
    account_number character varying
(14) COLLATE pg_catalog."default",
    balance numeric
(18,2) DEFAULT 0.0,
    created timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    currency character varying
(5) COLLATE pg_catalog."default" DEFAULT 'NGN'::character varying,
    status character varying
(20) COLLATE pg_catalog."default" DEFAULT 'ACTIVE'::character varying,
    udated timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    customer_id uuid DEFAULT uuid_generate_v4
(),
    CONSTRAINT wallet_pkey PRIMARY KEY
(id),
    CONSTRAINT uk_lrj0n3neixg5sqhncd7s4qg39 UNIQUE
(account_number),
    CONSTRAINT fk92p0rhbnmhnoiv2v4sxc8kfxf FOREIGN KEY
(customer_id)
        REFERENCES public.customer
(customer_id) MATCH SIMPLE
        ON
UPDATE NO ACTION
        ON
DELETE NO ACTION,
    CONSTRAINT wallet_currency_check
CHECK
(currency::text = ANY
(ARRAY['NGN'::character varying, 'USD'::character varying, 'GHS'::character varying, 'KES'::character varying]::text[])),
    CONSTRAINT wallet_status_check CHECK
(status::text = 'ACTIVE'::text)
)

create table
if not exists wallet_transaction
(
    id                   uuid        not null
        primary key,
    account_number       varchar
(14),
    amount               numeric
(18, 2) default 0.0,
    signed_amount        numeric
(18, 2) default 0.0,
    reference            varchar
(255) unique,
    transaction_type     varchar
(20) not null,
    balance_after        numeric
(18, 2) default 0.0,
    balance_before       numeric
(18, 2)   default 0.0,
    currency             varchar
(5)       default 'NGN'::character varying,
    wallet_id   uuid
        constraint fkm2axb1q8lpufx7lj50vvbu561
            references wallet,
    created              timestamp        default CURRENT_TIMESTAMP
);

CREATE INDEX idx_wallet_transaction_account_number
ON wallet_transaction(account_number);

INSERT INTO wallet
    (id, account_number)
VALUES(gen_random_uuid(), '0001'),
    (gen_random_uuid(), '0002'),
    (gen_random_uuid(), '0003');