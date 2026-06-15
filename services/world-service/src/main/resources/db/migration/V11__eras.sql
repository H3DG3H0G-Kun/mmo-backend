-- Seed eras 5–13 (authored data; see docs/lore/ERA_SEED_HANDOFF.md + ERAS.md) so the
-- chronological tale slate in content/ can import. New eras are not default_unlocked —
-- they are reached through the timeline engine / Tale completion unlocks.
--
-- ordinal is UNIQUE and is the timeline's chronological order. We renumber GOLDEN_AGE and
-- RUSTAVELI so the Living Timeline reads in true historical order (Nino..David precede the
-- Golden Age). Bump the two movers out of the way first to respect the UNIQUE constraint.

UPDATE era SET ordinal = ordinal + 1000 WHERE code IN ('GOLDEN_AGE', 'RUSTAVELI');

INSERT INTO era (id, code, name, ordinal, default_unlocked) VALUES
    (5,  'NINO',           'ნინოს ხანა — The Coming of the Light',          3,  false),
    (6,  'VAKHTANG',       'ვახტანგის ხანა — The Age of Vakhtang Gorgasali', 4,  false),
    (7,  'ABO',            'გრძელი ღამე — The Long Night',                  5,  false),
    (8,  'BAGRATI',        'მიწათა შეკრება — The Gathering of the Lands',    6,  false),
    (9,  'AGHMASHENEBELI', 'აღმაშენებლის ხანა — The Age of David the Builder', 7, false),
    (10, 'SUNDERING',      'შლა — The Sundering',                           10, false),
    (11, 'SAAKADZE',       'დიდი მოურავის ხანა — The Age of the Great Mouravi', 11, false),
    (12, 'EREKLE',         'უკანასკნელი სამეფო — The Last Kingdom',          12, false),
    (13, 'MODERN_WORD',    'აღორძინებული სიტყვა — The Reborn Word',          13, false);

UPDATE era SET ordinal = 8 WHERE code = 'GOLDEN_AGE';
UPDATE era SET ordinal = 9 WHERE code = 'RUSTAVELI';
