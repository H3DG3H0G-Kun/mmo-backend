INSERT INTO mmo_meta.seed_runs(seed_name) VALUES ('002_seed.sql');

INSERT INTO mmo_meta.dev_accounts(id, username)
VALUES ('11111111-1111-1111-1111-111111111111', 'dev_user')
    ON CONFLICT (username) DO NOTHING;
