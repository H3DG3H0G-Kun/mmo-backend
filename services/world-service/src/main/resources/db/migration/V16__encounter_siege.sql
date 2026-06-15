-- Tie a combat encounter to a siege: fighting a Distortion "for" a siege your clan contests
-- means victory feeds the clan's siege score (closing the combat -> siege loop).
ALTER TABLE encounter ADD COLUMN siege_id uuid REFERENCES siege (id);
