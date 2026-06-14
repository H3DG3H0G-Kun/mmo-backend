-- E14 Sieges: clans contest "places of power" — the Lineage 2 endgame and Georgian fortress
-- history. v1 models the lifecycle (declare -> contest -> resolve -> the holder changes) with
-- contribution scoring; real-time PvP combat will later feed the score. Backend-authoritative.

CREATE TABLE place_of_power (
    id              uuid        PRIMARY KEY,
    code            text        NOT NULL UNIQUE,
    name            text        NOT NULL,
    era_id          integer     NOT NULL REFERENCES era (id),
    holder_clan_id  uuid,                                  -- null = unclaimed
    captured_at     timestamptz
);

CREATE TABLE siege (
    id                uuid        PRIMARY KEY,
    place_id          uuid        NOT NULL REFERENCES place_of_power (id),
    status            text        NOT NULL,                -- DECLARED | ACTIVE | RESOLVED
    declaring_clan_id uuid        NOT NULL,
    winner_clan_id    uuid,
    declared_at       timestamptz NOT NULL DEFAULT now(),
    started_at        timestamptz,
    resolved_at       timestamptz
);
CREATE INDEX ix_siege_place ON siege (place_id);
-- At most one unresolved siege per place.
CREATE UNIQUE INDEX ux_siege_open_per_place ON siege (place_id) WHERE status <> 'RESOLVED';

CREATE TABLE siege_participant (
    id         uuid   PRIMARY KEY,
    siege_id   uuid   NOT NULL REFERENCES siege (id),
    clan_id    uuid   NOT NULL,
    score      bigint NOT NULL DEFAULT 0,
    UNIQUE (siege_id, clan_id)
);
CREATE INDEX ix_siege_participant_siege ON siege_participant (siege_id);

-- Seed a couple of contestable places (content as data).
INSERT INTO place_of_power (id, code, name, era_id) VALUES
    ('60000000-0000-0000-0000-000000000001', 'ARMAZTSIKHE', 'Armaztsikhe, the First Fortress', 1),
    ('60000000-0000-0000-0000-000000000002', 'NEKRESI',     'The Watchtower of Nekresi',       2);
