-- E09 Timeline: per-character era unlocks (the personal timeline) and a server-wide
-- restoration counter (the Living Timeline the whole server re-illuminates together).

-- A character has access to an era when it is default_unlocked OR explicitly unlocked here
-- (granted as a Tale-completion reward).
CREATE TABLE character_era_unlock (
    id           uuid        PRIMARY KEY,
    character_id uuid        NOT NULL,
    era_id       integer     NOT NULL REFERENCES era (id),
    unlocked_at  timestamptz NOT NULL DEFAULT now(),
    UNIQUE (character_id, era_id)
);
CREATE INDEX ix_era_unlock_character ON character_era_unlock (character_id);

-- Server-wide count of memories restored per era (the Living Timeline).
CREATE TABLE timeline_progress (
    era_id         integer PRIMARY KEY REFERENCES era (id),
    restored_count bigint  NOT NULL DEFAULT 0
);

-- Seed a progress row per existing era so increments are simple updates.
INSERT INTO timeline_progress (era_id, restored_count)
SELECT id, 0 FROM era;
