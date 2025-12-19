import type { Pageable } from '../types/pageTypes';
import type { RecipeSearchCriteria } from '../types/recipeTypes';
import {
  RecipeControllerService,
  type RecipeDetailsResponse,
} from './generated';

export const fetchRecipes = async (
  pageable?: Pageable,
  criteria?: RecipeSearchCriteria
) => {
  const sort: string[] =
    (pageable?.sort && [pageable.sort.sortBy + ',' + pageable.sort.order]) ||
    [];

  return await RecipeControllerService.getRecipes({
    ...pageable,
    sort,
    ...criteria,
  });
};

export const fetchRecipe = async (
  slug: string
): Promise<RecipeDetailsResponse> => {
  return await RecipeControllerService.getRecipe({ slug });
};
