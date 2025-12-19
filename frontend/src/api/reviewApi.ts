import type { Pageable } from '../types/pageTypes';
import { ReviewControllerService } from './generated';

export const fetchReviews = async (recipeSlug: string, pageable?: Pageable) => {
  const sort: string[] =
    (pageable?.sort && [pageable.sort.sortBy + ',' + pageable.sort.order]) ||
    [];

  return await ReviewControllerService.getAllReviews({
    slug: recipeSlug,
    ...pageable,
    sort,
  });
};
