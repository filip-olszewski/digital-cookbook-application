DO $$
DECLARE
    t text;
BEGIN
    FOREACH t IN ARRAY ARRAY['users', 'categories', 'tags', 'ingredients', 'recipes', 'reviews', 'favourites', 'steps', 'recipe_ingredients']
    LOOP
        EXECUTE format('ALTER TABLE %I 
                        ALTER COLUMN created_at TYPE TIMESTAMP WITH TIME ZONE USING created_at::timestamp with time zone,
                        ALTER COLUMN updated_at TYPE TIMESTAMP WITH TIME ZONE USING updated_at::timestamp with time zone,
                        ALTER COLUMN deleted_at TYPE TIMESTAMP WITH TIME ZONE USING deleted_at::timestamp with time zone', t);
    END LOOP;
END$$;