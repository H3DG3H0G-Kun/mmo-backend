-- E08 Party system: small groups of Watchers who adventure together (the social fabric the
-- Lineage 2 world feeling is built on). A character is in at most one party at a time.

CREATE TABLE party (
    id                  uuid        PRIMARY KEY,
    leader_character_id uuid        NOT NULL,
    max_size            integer     NOT NULL DEFAULT 5,
    status              text        NOT NULL DEFAULT 'OPEN',  -- OPEN | CLOSED | DISBANDED
    created_at          timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE party_member (
    id           uuid        PRIMARY KEY,
    party_id     uuid        NOT NULL REFERENCES party (id),
    character_id uuid        NOT NULL UNIQUE,               -- one party per character
    joined_at    timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ix_party_member_party ON party_member (party_id);
