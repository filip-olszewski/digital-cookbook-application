import { keepPreviousData, useQuery } from '@tanstack/react-query';
import { fetchRecipes } from '../api/recipeApi';
import type { RecipeSummary } from '../types/recipeTypes';

export const useRecipes = () => {
  const { data, error, isPending } = useQuery({
    queryKey: ['recipes'],
    queryFn: () => fetchRecipes(),
    placeholderData: keepPreviousData,
  });

  return { data, error, isPending };
};
