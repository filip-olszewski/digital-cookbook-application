ALTER TABLE recipes
ADD COLUMN favourite_count INTEGER DEFAULT 0 NOT NULL;

UPDATE recipes r SET favourite_count = (
    SELECT COUNT(*) FROM favourites f
    WHERE f.recipe_id = r.id
);