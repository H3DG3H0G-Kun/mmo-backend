-- World Echoes: public, server-shared runs of a Tale that many Watchers converge on (the
-- communal high, vs. the intimate solo/party instances). Anyone contributes; when the
-- collective progress reaches the goal, the memory is restored for every participant.

CREATE TABLE world_echo (
    id          uuid        PRIMARY KEY,
    tale_id     uuid        NOT NULL REFERENCES tale (id),
    status      text        NOT NULL,                      -- ACTIVE | RESOLVED
    goal        bigint      NOT NULL,
    progress    bigint      NOT NULL DEFAULT 0,
    started_at  timestamptz NOT NULL DEFAULT now(),
    resolved_at timestamptz
);
-- At most one active echo per tale.
CREATE UNIQUE INDEX ux_world_echo_active_per_tale ON world_echo (tale_id) WHERE status = 'ACTIVE';

CREATE TABLE world_echo_participation (
    id            uuid   PRIMARY KEY,
    world_echo_id uuid   NOT NULL REFERENCES world_echo (id),
    character_id  uuid   NOT NULL,
    contribution  bigint NOT NULL DEFAULT 0,
    UNIQUE (world_echo_id, character_id)
);
CREATE INDEX ix_world_echo_participation_echo ON world_echo_participation (world_echo_id);
