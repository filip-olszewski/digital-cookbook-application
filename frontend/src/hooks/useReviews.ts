import { keepPreviousData, useQuery } from '@tanstack/react-query';
import type { Pageable } from '../types/pageTypes';
import { fetchReviews } from '../api/reviewApi';

export const useReviews = (
  recipeSlug: string | undefined,
  pageable?: Pageable
) => {
  const page = pageable?.page ?? 0;
  const size = pageable?.size ?? 5;
  const sort = pageable?.sort ?? { sortBy: 'postedAt', order: 'desc' };

  const { data, error, isPending } = useQuery({
    queryKey: ['reviews', recipeSlug, page, size, sort],
    queryFn: () => fetchReviews(recipeSlug!, { page, size, sort }),
    placeholderData: keepPreviousData,
    staleTime: 1000 * 60,
    enabled: !!recipeSlug,
  });

  return { data, error, isPending };
};
