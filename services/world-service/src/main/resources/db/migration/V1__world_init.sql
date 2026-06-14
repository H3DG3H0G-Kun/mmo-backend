-- World bounded context (schema "game"): eras of the History Layer and player characters.

-- Eras are the persistent shared-world time layers (Parnavaz -> ... -> mythic).
-- "ordinal" is the timeline order; "default_unlocked" marks where new Watchers may begin.
CREATE TABLE era (
    id               integer     PRIMARY KEY,
    code             text        NOT NULL UNIQUE,
    name             text        NOT NULL,
    ordinal          integer     NOT NULL UNIQUE,
    default_unlocked boolean     NOT NULL DEFAULT false
);

-- A player character (a "Watcher" soul). Owned by an auth account (account_id from the JWT).
-- "character" is a reserved word in SQL, hence player_character.
CREATE TABLE player_character (
    id              uuid        PRIMARY KEY,
    account_id      uuid        NOT NULL,
    name            text        NOT NULL,
    current_era_id  integer     NOT NULL REFERENCES era (id),
    created_at      timestamptz NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX ux_character_name_lower ON player_character (lower(name));
CREATE INDEX ix_character_account ON player_character (account_id);

-- Seed the timeline skeleton. Only the first era is open to new Watchers; the rest are
-- unlocked later through the timeline engine. (Content, authored as data — not in Java.)
INSERT INTO era (id, code, name, ordinal, default_unlocked) VALUES
    (1, 'PARNAVAZ',   'The Age of Parnavaz',        1, true),
    (2, 'EARLY_KING', 'The Early Kingdoms',         2, false),
    (3, 'GOLDEN_AGE', 'The Golden Age',             3, false),
    (4, 'RUSTAVELI',  'The Myth-Weave of the Word', 4, false);
