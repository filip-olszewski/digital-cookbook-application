import { keepPreviousData, useQuery } from '@tanstack/react-query';
import type { CategorySearchCriteria } from '../types/categoryTypes';
import { fetchCategories } from '../api/categoryApi';

export const useCategories = (criteria: CategorySearchCriteria) => {
  const { data, isError, isPending } = useQuery({
    queryKey: ['categories', criteria],
    queryFn: () => fetchCategories(criteria),
    placeholderData: keepPreviousData,
    staleTime: 60 * 60 * 1000,
  });

  return { data, isError, isPending };
};
