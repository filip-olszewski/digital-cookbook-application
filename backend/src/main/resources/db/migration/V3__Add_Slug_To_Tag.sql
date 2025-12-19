DROP INDEX IF EXISTS idx_tag_label;

ALTER TABLE tags ADD COLUMN slug VARCHAR(255);

UPDATE tags
SET slug = LOWER(REGEXP_REPLACE(TRIM(label), '[^a-zA-Z0-9]+', '-', 'g'))
WHERE slug IS NULL;

ALTER TABLE tags ALTER COLUMN slug SET NOT NULL;

CREATE UNIQUE INDEX idx_tag_slug ON tags (slug) WHERE deleted_at IS NULL;