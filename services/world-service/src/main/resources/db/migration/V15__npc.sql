-- Server-spawned NPCs (vendors, heralds, townsfolk). They appear in the realtime presence
-- stream (WORLD_SNAPSHOT / ENTITY_JOINED) with kind=NPC and a role, so the client can render
-- and proximity-interact. Static for v1 (they don't move). Content as data.

CREATE TABLE npc (
    id            uuid    PRIMARY KEY,
    code          text    NOT NULL UNIQUE,
    name          text    NOT NULL,
    role          text    NOT NULL,                 -- VENDOR | HERALD | TOWNSFOLK | GUARD
    era_id        integer NOT NULL REFERENCES era (id),
    location_code text    NOT NULL,                 -- the location they stand at (matches location.code)
    x             double precision NOT NULL DEFAULT 0,
    y             double precision NOT NULL DEFAULT 0
);
CREATE INDEX ix_npc_era ON npc (era_id);

-- Seed a small living town in PARNAVAZ (Mtskheta + the throne hall). Vendors tie to the
-- existing economy/market (the client opens the market when interacting with a VENDOR).
INSERT INTO npc (id, code, name, role, era_id, location_code, x, y) VALUES
    ('c1000000-0000-0000-0000-000000000001', 'npc_pharna',  'ფარნა მოვაჭრე — Pharna the Trader', 'VENDOR',    1, 'mtskheta',    1.0,  0.5),
    ('c1000000-0000-0000-0000-000000000002', 'npc_herald',  'მაუწყებელი — The King''s Herald',   'HERALD',    1, 'throne_hall', 3.0,  1.8),
    ('c1000000-0000-0000-0000-000000000003', 'npc_elder',   'მცხეთელი უხუცესი — An Elder of Mtskheta', 'TOWNSFOLK', 1, 'mtskheta', -1.2, 0.4);
