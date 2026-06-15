-- Retire the V2 placeholder Parnavaz saga. The authored SAGA_FARNAVAZ / TALE_FARNAVAZ_CROWN
-- (imported from content/the-first-crown.json) supersedes it and resonates at the same
-- ERA=PARNAVAZ + PLACE=throne_hall, so leaving the placeholder would double-resonate.
-- Delete in FK order. Keyed on the placeholder saga code so it is a no-op if already gone.

CREATE TEMP TABLE _retire_tales ON COMMIT DROP AS
    SELECT id FROM tale WHERE saga_id IN (SELECT id FROM saga WHERE code = 'SAGA_PARNAVAZ');

CREATE TEMP TABLE _retire_beats ON COMMIT DROP AS
    SELECT id FROM beat WHERE tale_id IN (SELECT id FROM _retire_tales);

DELETE FROM world_echo_participation
    WHERE world_echo_id IN (SELECT id FROM world_echo WHERE tale_id IN (SELECT id FROM _retire_tales));
DELETE FROM world_echo      WHERE tale_id IN (SELECT id FROM _retire_tales);
DELETE FROM tale_instance   WHERE tale_id IN (SELECT id FROM _retire_tales);
DELETE FROM tale_progress   WHERE tale_id IN (SELECT id FROM _retire_tales);
DELETE FROM trigger_condition WHERE tale_id IN (SELECT id FROM _retire_tales);
DELETE FROM beat_edge       WHERE from_beat_id IN (SELECT id FROM _retire_beats)
                               OR to_beat_id   IN (SELECT id FROM _retire_beats);
DELETE FROM beat            WHERE tale_id IN (SELECT id FROM _retire_tales);
DELETE FROM tale            WHERE id IN (SELECT id FROM _retire_tales);
DELETE FROM saga            WHERE code = 'SAGA_PARNAVAZ';
