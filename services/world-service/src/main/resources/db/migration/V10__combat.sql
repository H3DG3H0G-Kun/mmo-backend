-- Combat / encounter engine: backend-authoritative fights against Distortions (Echo/Aspect
-- bosses). v1 is deterministic and turn-based over REST; the client sends ATTACK intentions,
-- the backend resolves all damage and decides win/lose. Combat is the verb behind WARD beats
-- and (later) feeds siege contribution.

CREATE TABLE enemy_def (
    id           uuid    PRIMARY KEY,
    code         text    NOT NULL UNIQUE,
    name         text    NOT NULL,
    max_hp       integer NOT NULL,
    attack_power integer NOT NULL,
    gold_reward  bigint  NOT NULL DEFAULT 0
);

CREATE TABLE encounter (
    id            uuid        PRIMARY KEY,
    enemy_def_id  uuid        NOT NULL REFERENCES enemy_def (id),
    character_id  uuid        NOT NULL,
    enemy_hp      integer     NOT NULL,
    character_hp  integer     NOT NULL,
    status        text        NOT NULL,                   -- ACTIVE | WON | LOST
    started_at    timestamptz NOT NULL DEFAULT now(),
    ended_at      timestamptz
);
CREATE INDEX ix_encounter_character ON encounter (character_id, status);

-- Seed a couple of Distortions (content as data). Echoes/Aspects, never the beloved heroes.
INSERT INTO enemy_def (id, code, name, max_hp, attack_power, gold_reward) VALUES
    ('80000000-0000-0000-0000-000000000001', 'ECHO_OF_THE_CROWN',  'Echo of the Crown',   100, 10, 50),
    ('80000000-0000-0000-0000-000000000002', 'ASPECT_OF_BETRAYAL', 'Aspect of Betrayal',  160, 18, 90);
