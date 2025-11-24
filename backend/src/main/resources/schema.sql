CREATE UNIQUE INDEX recipe_slug
ON recipes (slug)
WHERE deleted_at IS NULL;

CREATE UNIQUE INDEX tag_label
ON tags (label)
WHERE deleted_at IS NULL;