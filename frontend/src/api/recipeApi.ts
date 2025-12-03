import type { RecipeSummary } from '../types/recipeTypes';

export const fetchRecipes = async () => {
  let url = import.meta.env.VITE_RECIPE_API_URL;

  if (!url) {
    throw new Error('URL is not specified!');
  }

  const res = await fetch(url);

  if (!res.ok) {
    throw new Error('Failed to fetch recipes (' + res.status + ')');
  }

  return await res.json();
};
