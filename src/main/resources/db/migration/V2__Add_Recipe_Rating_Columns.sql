ALTER TABLE recipes
ADD COLUMN average_rating DOUBLE PRECISION DEFAULT 0.0 NOT NULL,
ADD COLUMN review_count INTEGER DEFAULT 0 NOT NULL;

UPDATE recipes r
SET
    review_count = (
        SELECT COUNT(*)
        FROM reviews rv
        WHERE rv.recipe_id = r.id
    ),
    average_rating = (
        SELECT COALESCE(AVG(rv.rating), 0.0)
        FROM reviews rv
        WHERE rv.recipe_id = r.id
    );