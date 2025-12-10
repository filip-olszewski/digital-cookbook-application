import type { RecipeSummary } from '../types/recipeTypes';
import { RecipeControllerService, type PageRecipeSummaryResponse, type RecipeDetailsResponse, type RecipeSearchCriteria } from './generated';

export const fetchRecipes = async (
  criteria: RecipeSearchCriteria,
  page?: number,
  size?: number,
  sort?: Array<string>
): Promise<PageRecipeSummaryResponse> => {
  return await RecipeControllerService.getRecipes(page, size, sort);
};

export const fetchRecipe = async (slug: string): Promise<RecipeDetailsResponse> => {
  return await RecipeControllerService.getRecipe(slug);
}


