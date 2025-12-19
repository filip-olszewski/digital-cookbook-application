import { useEffect, useState } from 'react';
import { useRecipes } from '../hooks/useRecipes';
import { useDebounce } from '../hooks/useDebounce';
import { useRecipeParams } from '../hooks/useRecipeParams';
import RecipeCard from '../components/RecipeCard';
import RecipeFilters from '../components/filters/RecipeFilters';
import SearchBar from '../components/filters/SearchBar';
import RecipeSort from '../components/filters/RecipeSort';
import { XCircleIcon } from '@heroicons/react/24/outline'; // Optional icon
import type { RecipeSummaryResponse } from '../api/generated';

const BrowseRecipesPage = () => {
  const { searchCriteria, pageable, updateParams } = useRecipeParams();
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearchTerm = useDebounce(searchTerm);

  useEffect(() => {
    if (debouncedSearchTerm !== searchCriteria.name) {
      updateParams({ name: debouncedSearchTerm, page: 0 });
    }
  }, [debouncedSearchTerm]);

  const { data, error, isPending } = useRecipes(pageable, searchCriteria);

  const recipes: RecipeSummaryResponse[] = data?.content || [];

  const displayRecipes = [...recipes, ...recipes, ...recipes];

  return (
    <div className='max-w-7xl mx-auto px-6 pt-32 pb-20'>
      <div className='flex flex-col md:flex-row md:items-end justify-between gap-4 mb-8'>
        <div>
          <h1 className='text-3xl md:text-4xl font-extrabold text-slate-900 tracking-tight'>
            Browse Recipes
          </h1>
          <p className='text-slate-500 mt-2'>
            Find the perfect meal for any occasion.
          </p>
        </div>

        {!isPending && (
          <div className='text-sm font-medium text-slate-500 bg-slate-100 px-3 py-1 rounded-full'>
            {data?.totalElements || 0} results found
          </div>
        )}
      </div>

      <div className='flex flex-col lg:flex-row gap-8 items-start'>
        <aside className='w-full lg:w-72 shrink-0 lg:sticky lg:top-24'>
          <RecipeFilters
            filters={searchCriteria}
            onChange={filters => updateParams({ ...filters, page: 0 })}
          />
        </aside>

        <div className='flex-1 w-full'>
          <div className='flex flex-col sm:flex-row gap-4 mb-6'>
            <div className='flex-1'>
              <SearchBar value={searchTerm} onChange={setSearchTerm} />
            </div>
            <div className='w-full sm:w-auto'>
              <RecipeSort
                currentSort={pageable.sort}
                onSortChange={sort =>
                  updateParams({ sortBy: sort.sortBy, order: sort.order })
                }
              />
            </div>
          </div>

          <div className='min-h-[50vh]'>
            {isPending ? (
              <div className='grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-6 animate-pulse'>
                {[...Array(6)].map((_, i) => (
                  <div
                    key={i}
                    className='aspect-[4/3] bg-gray-100 rounded-2xl'
                  ></div>
                ))}
              </div>
            ) : error ? (
              <div className='flex flex-col items-center justify-center py-20 text-center'>
                <div className='bg-red-50 p-4 rounded-full mb-4'>
                  <XCircleIcon className='size-8 text-red-500' />
                </div>
                <h2 className='text-xl font-bold text-slate-900'>
                  Error loading recipes
                </h2>
                <p className='text-slate-500 mt-2 max-w-md'>{error.message}</p>
                <button
                  onClick={() => window.location.reload()}
                  className='mt-6 text-sm font-semibold text-blue-600 hover:underline'
                >
                  Try Again
                </button>
              </div>
            ) : displayRecipes.length === 0 ? (
              <div className='flex flex-col items-center justify-center py-20 text-center border-2 border-dashed border-slate-200 rounded-2xl bg-slate-50/50'>
                <div className='bg-white p-4 rounded-full shadow-sm mb-4'>
                  <span className='text-4xl'>🍳</span>
                </div>
                <h3 className='text-lg font-bold text-slate-900'>
                  No recipes found
                </h3>
                <p className='text-slate-500 mt-1 mb-6'>
                  Try adjusting your search or filters to find what you're
                  looking for.
                </p>
                <button
                  onClick={() => {
                    setSearchTerm('');
                    updateParams({ name: '', page: 0 });
                  }}
                  className='px-4 py-2 bg-white border border-slate-300 rounded-lg text-sm font-medium text-slate-700 hover:bg-slate-50 transition-colors'
                >
                  Clear all filters
                </button>
              </div>
            ) : (
              <div className='grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-6'>
                {displayRecipes.map((recipe, index) => (
                  <RecipeCard key={`${recipe.id}-${index}`} recipe={recipe} />
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default BrowseRecipesPage;
