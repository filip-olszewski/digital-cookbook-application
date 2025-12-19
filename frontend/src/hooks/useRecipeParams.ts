import { replace, useSearchParams } from 'react-router';
import type { RecipeSearchCriteria } from '../types/recipeTypes';
import type { Pageable } from '../types/pageTypes';
import { useMemo } from 'react';

export const useRecipeParams = () => {
  const [searchParams, setSearchParams] = useSearchParams();

  const searchCriteria = useMemo<RecipeSearchCriteria>(() => {
    return {
      name: searchParams.get('name') || undefined,
      maxPrepTime: searchParams.get('maxPrepTime')
        ? Number(searchParams.get('maxPrepTime'))
        : undefined,
      minRating: searchParams.get('minRating')
        ? Number(searchParams.get('minRating'))
        : undefined,
      tags: searchParams.get('tags')
        ? searchParams.get('tags')?.split(',')
        : undefined,
      categories: searchParams.get('categories')
        ? searchParams.get('categories')?.split(',')
        : undefined,
    };
  }, [searchParams]);

  const pageable = useMemo<Pageable>(() => {
    return {
      page: Number(searchParams.get('page')) || 0,
      size: Number(searchParams.get('size')) || 24,
      sort: {
        sortBy: searchParams.get('sortBy') || 'name',
        order: (searchParams.get('order') as 'asc' | 'desc') || 'asc',
      },
    };
  }, [searchParams]);

  const updateParams = (updates: Record<string, any>) => {
    setSearchParams(
      prev => {
        const newParams = new URLSearchParams(prev);
        Object.entries(updates).forEach(([key, value]) => {
          if (
            value === undefined ||
            value === null ||
            value === '' ||
            (Array.isArray(value) && value.length === 0)
          ) {
            newParams.delete(key);
          } else {
            newParams.set(key, String(value));
          }
        });
        return newParams;
      },
      { replace: true }
    );
  };

  return { searchCriteria, pageable, updateParams };
};
