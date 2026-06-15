-- Authored, human-readable label for a beat choice (the text a CHOOSE button shows).
-- Optional: when null, the client/BeatView falls back to humanizing the choice key.
ALTER TABLE beat_edge ADD COLUMN label text;
