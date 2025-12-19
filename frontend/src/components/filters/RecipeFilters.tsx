import type { RecipeSearchCriteria } from '../../types/recipeTypes';
import PillsFilter from './PillsFilter';
import SliderFilter from './SliderFilter';
import RatingFilter from './RatingFilter';
import { useCategories } from '../../hooks/useCategories';
import { useTags } from '../../hooks/useTags';
import { useEffect, useState } from 'react';
import { useDebounce } from '../../hooks/useDebounce';

type RecipeFiltersProps = {
  filters: RecipeSearchCriteria;
  onChange: (filters: RecipeSearchCriteria) => void;
};

const SLIDER_MAX_VALUE = 120;

const RecipeFilters = ({ filters, onChange }: RecipeFiltersProps) => {
  const { data: categories = [] } = useCategories({});
  const { data: tags = [] } = useTags();

  const [localMaxTime, setLocalMaxTime] = useState<number>(
    filters.maxPrepTime ?? SLIDER_MAX_VALUE
  );

  const debouncedMaxPrepTime = useDebounce(localMaxTime, 300);

  useEffect(() => {
    const apiValue =
      debouncedMaxPrepTime === SLIDER_MAX_VALUE
        ? undefined
        : debouncedMaxPrepTime;

    if (apiValue !== filters.maxPrepTime) {
      updateFilter({ maxPrepTime: apiValue });
    }
  }, [debouncedMaxPrepTime]);

  const updateFilter = (patch: Partial<RecipeSearchCriteria>) => {
    onChange({ ...filters, ...patch });
  };

  const resetFilters = () => {
    onChange({});
  };

  return (
    <div className='bg-white p-6 rounded-lg border border-slate-200 flex flex-col gap-6'>
      <SliderFilter
        selectedMaxTime={localMaxTime}
        onChange={val => setLocalMaxTime(val ?? SLIDER_MAX_VALUE)}
        sliderMaxValue={SLIDER_MAX_VALUE}
      />

      <hr className='border-gray-100' />

      <RatingFilter
        minRating={filters.minRating}
        onChange={val => updateFilter({ minRating: val })}
      />

      <hr className='border-gray-100' />

      <PillsFilter
        title='Categories'
        options={categories}
        getValue={c => c.slug}
        getLabel={c => c.name}
        data={filters.categories}
        onChange={values => updateFilter({ categories: values })}
        color='amber'
      />

      <hr className='border-gray-100' />

      <PillsFilter
        title='Tags'
        options={tags}
        getValue={t => t.slug}
        getLabel={t => t.label}
        data={filters.tags}
        onChange={values => updateFilter({ tags: values })}
        color='emerald'
      />

      <button
        onClick={resetFilters}
        className='mt-4 text-sm text-gray-500 hover:text-gray-900 underline self-start'
      >
        Reset all filters
      </button>
    </div>
  );
};

export default RecipeFilters;
