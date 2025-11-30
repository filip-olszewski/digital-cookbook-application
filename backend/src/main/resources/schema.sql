-- Unique slug for (active) recipes
CREATE UNIQUE INDEX IF NOT EXISTS idx_recipe_slug
ON recipes (slug)
WHERE deleted_at IS NULL;

-- Unique email for (active) users
CREATE UNIQUE INDEX IF NOT EXISTS idx_user_email
ON users (email)
WHERE deleted_at IS NULL;

-- Unique labels for (active) tags
CREATE UNIQUE INDEX IF NOT EXISTS idx_tag_label
ON tags (label)
WHERE deleted_at IS NULL;

-- Unique names for (active) ingredients
CREATE UNIQUE INDEX IF NOT EXISTS idx_ingredient_name
ON ingredients (name)
WHERE deleted_at IS NULL;

-- User can review single recipe only once
CREATE UNIQUE INDEX IF NOT EXISTS idx_review_user_recipe
ON reviews (user_id, recipe_id)
WHERE deleted_at IS NULL;

-- User can like recipe only once
CREATE UNIQUE INDEX IF NOT EXISTS idx_favourite_user_recipe
ON favourites (user_id, recipe_id)
WHERE deleted_at IS NULL;

-- Recipe can contain single ingredient only once
CREATE UNIQUE INDEX IF NOT EXISTS idx_recipe_ingredient_unique
ON recipe_ingredients (recipe_id, ingredient_id);

-- Recipe can only contain one instruction step at one step_order
CREATE UNIQUE INDEX IF NOT EXISTS idx_recipe_step_unique
ON steps (step_order, recipe_id)