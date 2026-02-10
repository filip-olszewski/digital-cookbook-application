DROP INDEX IF EXISTS idx_tag_slug;

ALTER TABLE tags
DROP COLUMN slug;

CREATE UNIQUE INDEX idx_tag_label ON tags (label) WHERE deleted_at IS NULL;