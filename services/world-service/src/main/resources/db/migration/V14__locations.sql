-- E?? World geography: named Locations per era + a travel graph. A location's `code` matches the
-- PLACE codes authored tales resonate at (e.g. throne_hall, mtskheta), so a character standing at
-- a location can open the tales that resonate there. Codes are unique PER ERA (Mtskheta recurs
-- across eras as distinct rows). Content as data — geography grows by seeding, not engine changes.

CREATE TABLE location (
    id          uuid    PRIMARY KEY,
    code        text    NOT NULL,
    name        text    NOT NULL,
    era_id      integer NOT NULL REFERENCES era (id),
    type        text    NOT NULL,                 -- TOWN | LANDMARK | SACRED | PLACE_OF_POWER | WILDS
    region      text    NOT NULL,
    x           double precision NOT NULL DEFAULT 0,
    y           double precision NOT NULL DEFAULT 0,
    description text,
    UNIQUE (era_id, code)
);
CREATE INDEX ix_location_era ON location (era_id);

-- Directed edges of the travel graph (seed both directions for two-way roads).
CREATE TABLE location_link (
    id               uuid PRIMARY KEY,
    from_location_id uuid NOT NULL REFERENCES location (id),
    to_location_id   uuid NOT NULL REFERENCES location (id),
    UNIQUE (from_location_id, to_location_id)
);

-- A character's current location within its era (null = just arrived / not yet placed).
ALTER TABLE player_character ADD COLUMN current_location_id uuid REFERENCES location (id);

-- ---------------------------------------------------------------------------
-- Seed. PARNAVAZ (era 1) is the fully-connected, playable era; a few landmark
-- anchors are seeded for later eras so their tales are reachable once travelled to.
-- (Richer per-era geography is ongoing authored data.)
-- ---------------------------------------------------------------------------
INSERT INTO location (id, code, name, era_id, type, region, x, y, description) VALUES
    ('a1000000-0000-0000-0000-000000000001', 'mtskheta',     'მცხეთა — Mtskheta',          1, 'TOWN',     'KARTLI',    0,  0,  'The old capital at the meeting of the waters.'),
    ('a1000000-0000-0000-0000-000000000002', 'throne_hall',  'სამეფო დარბაზი — The Throne Hall', 1, 'LANDMARK', 'KARTLI', 3,  1,  'Where the first crown of Kartli is raised.'),
    ('a1000000-0000-0000-0000-000000000003', 'armazi_mount', 'არმაზის მთა — Mount Armazi', 1, 'SACRED',   'KARTLI',    6,  4,  'The idol-mount of the old gods above Mtskheta.'),
    ('a1000000-0000-0000-0000-000000000004', 'peak',         'მწვერვალი — The Storm-Peak', 1, 'WILDS',    'HIGHLANDS', 13, 12, 'A Caucasus summit where the bound titan is remembered.'),
    ('a1000000-0000-0000-0000-000000000005', 'mountain',     'მთა — The High Mountain',    1, 'WILDS',    'HIGHLANDS', 11, 9,  'The sacred heights, reachable when the storm rises.'),
    -- Later-era anchors (single landmark each for now).
    ('a5000000-0000-0000-0000-000000000001', 'mtskheta',     'მცხეთა — Mtskheta',          5, 'TOWN',     'KARTLI',    0,  0,  'Mtskheta in the age of the Light; Svetitskhoveli rises.'),
    ('a6000000-0000-0000-0000-000000000001', 'tbilisi_springs','თბილისის აბანო — The Warm Springs', 6, 'LANDMARK', 'TBILISI', 0, 0, 'The springs where Vakhtang founds the city.'),
    ('a9000000-0000-0000-0000-000000000001', 'didgori',      'დიდგორი — Didgori',          9, 'LANDMARK', 'KARTLI',    0,  0,  'The field of the Wondrous Victory, 1121.'),
    ('a3000000-0000-0000-0000-000000000001', 'vardzia',      'ვარძია — Vardzia',           3, 'LANDMARK', 'SAMTSKHE',  0,  0,  'The cave-city of Tamar''s age.'),
    ('a3000000-0000-0000-0000-000000000002', 'court',        'სამეფო კარი — The Royal Court', 3, 'LANDMARK', 'KARTLI', 4, 2, 'Tamar''s court at the apex of the kingdom.'),
    ('a1200000-0000-0000-0000-000000000001', 'krtsanisi',    'კრწანისი — Krtsanisi',       12, 'LANDMARK', 'TBILISI',  0,  0,  'Where the Three Hundred Aragvians stood, 1795.');

INSERT INTO location_link (id, from_location_id, to_location_id) VALUES
    -- Mtskheta <-> Throne Hall
    ('b1000000-0000-0000-0000-000000000001', 'a1000000-0000-0000-0000-000000000001', 'a1000000-0000-0000-0000-000000000002'),
    ('b1000000-0000-0000-0000-000000000002', 'a1000000-0000-0000-0000-000000000002', 'a1000000-0000-0000-0000-000000000001'),
    -- Mtskheta <-> Mount Armazi
    ('b1000000-0000-0000-0000-000000000003', 'a1000000-0000-0000-0000-000000000001', 'a1000000-0000-0000-0000-000000000003'),
    ('b1000000-0000-0000-0000-000000000004', 'a1000000-0000-0000-0000-000000000003', 'a1000000-0000-0000-0000-000000000001'),
    -- Mount Armazi <-> Mountain
    ('b1000000-0000-0000-0000-000000000005', 'a1000000-0000-0000-0000-000000000003', 'a1000000-0000-0000-0000-000000000005'),
    ('b1000000-0000-0000-0000-000000000006', 'a1000000-0000-0000-0000-000000000005', 'a1000000-0000-0000-0000-000000000003'),
    -- Mountain <-> Storm-Peak
    ('b1000000-0000-0000-0000-000000000007', 'a1000000-0000-0000-0000-000000000005', 'a1000000-0000-0000-0000-000000000004'),
    ('b1000000-0000-0000-0000-000000000008', 'a1000000-0000-0000-0000-000000000004', 'a1000000-0000-0000-0000-000000000005'),
    -- Golden Age: Court <-> Vardzia
    ('b3000000-0000-0000-0000-000000000001', 'a3000000-0000-0000-0000-000000000002', 'a3000000-0000-0000-0000-000000000001'),
    ('b3000000-0000-0000-0000-000000000002', 'a3000000-0000-0000-0000-000000000001', 'a3000000-0000-0000-0000-000000000002');
