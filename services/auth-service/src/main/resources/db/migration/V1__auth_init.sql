-- Auth bounded context. Lives in its own schema so services stay decoupled.
CREATE TABLE account (
    id            uuid        PRIMARY KEY,
    username      text        NOT NULL,
    email         text,
    password_hash text        NOT NULL,
    roles         text        NOT NULL DEFAULT 'PLAYER',
    created_at    timestamptz NOT NULL DEFAULT now(),
    updated_at    timestamptz NOT NULL DEFAULT now()
);

-- Usernames are unique case-insensitively (so "Watcher" and "watcher" can't both exist).
CREATE UNIQUE INDEX ux_account_username_lower ON account (lower(username));
CREATE UNIQUE INDEX ux_account_email_lower ON account (lower(email)) WHERE email IS NOT NULL;
