-- E14 Clans: persistent "Orders / Circles of Watchers" — the social bedrock for the
-- Lineage 2 world feeling and the future siege/territory system. A character is in at
-- most one clan; clans have ranked membership.

CREATE TABLE clan (
    id                  uuid        PRIMARY KEY,
    name                text        NOT NULL,
    tag                 text        NOT NULL,
    leader_character_id uuid        NOT NULL,
    max_members         integer     NOT NULL DEFAULT 30,
    created_at          timestamptz NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX ux_clan_name_lower ON clan (lower(name));
CREATE UNIQUE INDEX ux_clan_tag_lower ON clan (lower(tag));

CREATE TABLE clan_member (
    id           uuid        PRIMARY KEY,
    clan_id      uuid        NOT NULL REFERENCES clan (id),
    character_id uuid        NOT NULL UNIQUE,            -- one clan per character
    rank         text        NOT NULL,                  -- LEADER | OFFICER | MEMBER
    joined_at    timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ix_clan_member_clan ON clan_member (clan_id);

CREATE TABLE clan_invite (
    id           uuid        PRIMARY KEY,
    clan_id      uuid        NOT NULL REFERENCES clan (id),
    character_id uuid        NOT NULL,                   -- the invitee
    invited_by   uuid        NOT NULL,
    created_at   timestamptz NOT NULL DEFAULT now(),
    UNIQUE (clan_id, character_id)
);
CREATE INDEX ix_clan_invite_character ON clan_invite (character_id);
