-- Minimal, safe infra-only init (does not assume your domain schema yet)
CREATE SCHEMA IF NOT EXISTS mmo_meta;

CREATE TABLE IF NOT EXISTS mmo_meta.seed_runs (
                                                  id           bigserial PRIMARY KEY,
                                                  seed_name    text NOT NULL,
                                                  executed_at  timestamptz NOT NULL DEFAULT now()
    );

-- A tiny table you can safely keep forever (future services can ignore it)
CREATE TABLE IF NOT EXISTS mmo_meta.dev_accounts (
                                                     id           uuid PRIMARY KEY,
                                                     username     text NOT NULL UNIQUE,
                                                     created_at   timestamptz NOT NULL DEFAULT now()
    );
