-- E08 (cont.) Co-op Tale instances: a party's shared run of a Tale. One active run per party;
-- shared current beat; on completion the reward fans out to every member (recorded as each
-- member's TaleProgress + era unlock).
CREATE TABLE tale_instance (
    id              uuid        PRIMARY KEY,
    tale_id         uuid        NOT NULL REFERENCES tale (id),
    party_id        uuid        NOT NULL,
    current_beat_id uuid        REFERENCES beat (id),
    status          text        NOT NULL,                  -- ACTIVE | COMPLETED
    started_at      timestamptz NOT NULL DEFAULT now(),
    completed_at    timestamptz
);
CREATE INDEX ix_tale_instance_party ON tale_instance (party_id, status);

-- At most one active run per party.
CREATE UNIQUE INDEX ux_tale_instance_active_party
    ON tale_instance (party_id) WHERE status = 'ACTIVE';
