-- E14 (cont.) Player economy: items, inventory, a gold wallet, and player-driven shop listings
-- (the face-to-face social economy of the Lineage 2 world). Server-authoritative.

CREATE TABLE item_def (
    id          uuid    PRIMARY KEY,
    code        text    NOT NULL UNIQUE,
    name        text    NOT NULL,
    description text    NOT NULL,
    stackable   boolean NOT NULL DEFAULT true,
    gatherable  boolean NOT NULL DEFAULT false
);

CREATE TABLE wallet (
    character_id uuid   PRIMARY KEY,
    gold         bigint NOT NULL DEFAULT 0
);

CREATE TABLE inventory_item (
    id           uuid   PRIMARY KEY,
    character_id uuid   NOT NULL,
    item_def_id  uuid   NOT NULL REFERENCES item_def (id),
    quantity     bigint NOT NULL,
    UNIQUE (character_id, item_def_id)
);
CREATE INDEX ix_inventory_character ON inventory_item (character_id);

CREATE TABLE shop_listing (
    id                  uuid        PRIMARY KEY,
    seller_character_id uuid        NOT NULL,
    item_def_id         uuid        NOT NULL REFERENCES item_def (id),
    quantity            bigint      NOT NULL,
    unit_price          bigint      NOT NULL,
    status              text        NOT NULL,             -- ACTIVE | SOLD | CANCELLED
    created_at          timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ix_shop_listing_status ON shop_listing (status);

-- Seed a small item catalogue (content as data).
INSERT INTO item_def (id, code, name, description, stackable, gatherable) VALUES
    ('70000000-0000-0000-0000-000000000001', 'MOUNTAIN_HERB', 'Mountain Herb', 'A fragrant herb from the Caucasus slopes.', true, true),
    ('70000000-0000-0000-0000-000000000002', 'IRON_ORE',      'Iron Ore',      'Raw ore, the bones of a fortress.',         true, true),
    ('70000000-0000-0000-0000-000000000003', 'MEMORY_RELIC',  'Memory Relic',  'A shard of restored memory.',               true, false);
