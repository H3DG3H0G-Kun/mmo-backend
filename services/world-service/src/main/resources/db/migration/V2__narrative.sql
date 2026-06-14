-- E07 Narrative engine: the universal Saga -> Tale -> Beat graph, composable Resonance
-- triggers, and per-character progress. Stories are DATA; the engine never names them.
-- (A "Tale" is the design doc's "Thread".)

CREATE TABLE saga (
    id       uuid    PRIMARY KEY,
    code     text    NOT NULL UNIQUE,
    title    text    NOT NULL,
    strand   text    NOT NULL,                          -- HISTORY | MYTH | WORD
    era_id   integer NOT NULL REFERENCES era (id),
    ordinal  integer NOT NULL
);

CREATE TABLE tale (
    id             uuid    PRIMARY KEY,
    saga_id        uuid    NOT NULL REFERENCES saga (id),
    code           text    NOT NULL UNIQUE,
    title          text    NOT NULL,
    tier           text    NOT NULL,                     -- TRUE_TALE | ECHO
    ordinal        integer NOT NULL,
    unlocks_era_id integer REFERENCES era (id)           -- era opened on completion (reward)
);

CREATE TABLE beat (
    id          uuid    PRIMARY KEY,
    tale_id     uuid    NOT NULL REFERENCES tale (id),
    code        text    NOT NULL,
    ordinal     integer NOT NULL,
    narration   text    NOT NULL,
    interaction text    NOT NULL,                         -- WITNESS | AID | WARD | CHOOSE | RESTORE
    terminal    boolean NOT NULL DEFAULT false,
    UNIQUE (tale_id, code)
);

-- Beats form a graph (branch + rejoin). choice_key is set only for edges out of a CHOOSE beat.
CREATE TABLE beat_edge (
    id           uuid PRIMARY KEY,
    from_beat_id uuid NOT NULL REFERENCES beat (id),
    to_beat_id   uuid NOT NULL REFERENCES beat (id),
    choice_key   text
);

-- A Tale's Resonance: ALL of its conditions must hold for the Tale to open (the Herald appears).
CREATE TABLE trigger_condition (
    id      uuid PRIMARY KEY,
    tale_id uuid NOT NULL REFERENCES tale (id),
    type    text NOT NULL,                                -- ERA | PLACE | STATE | PRIOR_TALE
    value   text NOT NULL
);
CREATE INDEX ix_trigger_condition_tale ON trigger_condition (tale_id);

-- One row per Watcher per Tale they have entered.
CREATE TABLE tale_progress (
    id              uuid        PRIMARY KEY,
    character_id    uuid        NOT NULL,
    tale_id         uuid        NOT NULL REFERENCES tale (id),
    current_beat_id uuid        REFERENCES beat (id),
    status          text        NOT NULL,                 -- IN_PROGRESS | COMPLETED
    started_at      timestamptz NOT NULL DEFAULT now(),
    completed_at    timestamptz,
    UNIQUE (character_id, tale_id)
);
CREATE INDEX ix_tale_progress_character ON tale_progress (character_id);

-- ---------------------------------------------------------------------------
-- Seed content (authored as data): Act 1 placeholder for the Parnavaz era.
-- Saga "Echoes of Parnavaz" -> True Tale "The First Crown", opened by being in the
-- Parnavaz era at the throne-hall anchor. Three beats: Witness -> Ward -> Witness(end).
-- Completing it unlocks era 2. No story logic in Java.
-- ---------------------------------------------------------------------------
INSERT INTO saga (id, code, title, strand, era_id, ordinal) VALUES
    ('10000000-0000-0000-0000-000000000001', 'SAGA_PARNAVAZ', 'Echoes of Parnavaz', 'HISTORY', 1, 1);

INSERT INTO tale (id, saga_id, code, title, tier, ordinal, unlocks_era_id) VALUES
    ('20000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001',
     'TALE_FIRST_CROWN', 'The First Crown', 'TRUE_TALE', 1, 2);

INSERT INTO beat (id, tale_id, code, ordinal, narration, interaction, terminal) VALUES
    ('30000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001',
     'ARRIVAL', 1, 'Unseen, you stand in the throne-hall as the first crown is raised over a young kingdom.', 'WITNESS', false),
    ('30000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000001',
     'WARD_THE_MEMORY', 2, 'A Distortion gnaws at the memory of the crowning. Hold the moment true.', 'WARD', false),
    ('30000000-0000-0000-0000-000000000003', '20000000-0000-0000-0000-000000000001',
     'IT_IS_REMEMBERED', 3, 'The memory steadies. The first king is remembered, and the era''s thread is whole.', 'WITNESS', true);

INSERT INTO beat_edge (id, from_beat_id, to_beat_id, choice_key) VALUES
    ('40000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000002', NULL),
    ('40000000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000003', NULL);

INSERT INTO trigger_condition (id, tale_id, type, value) VALUES
    ('50000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', 'ERA', 'PARNAVAZ'),
    ('50000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000001', 'PLACE', 'throne_hall');
