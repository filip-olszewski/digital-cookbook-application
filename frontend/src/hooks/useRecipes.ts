import { keepPreviousData, useQuery } from '@tanstack/react-query';
import type { RecipeSearchCriteria } from '../types/recipeTypes';
import { fetchRecipes } from '../api/recipeApi';
import type { Pageable } from '../types/pageTypes';

export const useRecipes = (
  pageable: Pageable,
  criteria: RecipeSearchCriteria
) => {
  const { data, error, isPending } = useQuery({
    queryKey: ['recipes', pageable, criteria],
    queryFn: () => fetchRecipes(pageable, criteria),
    placeholderData: keepPreviousData,
    staleTime: 5 * 1000,
  });

  return { data, error, isPending };
};
