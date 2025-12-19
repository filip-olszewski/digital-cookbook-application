import { ChevronDownIcon, ArrowsUpDownIcon } from '@heroicons/react/24/outline';
import type { PageableSort } from '../../types/pageTypes';

type RecipeSortProps = {
  currentSort: PageableSort;
  onSortChange: (sort: PageableSort) => void;
};

const SORT_OPTIONS = [
  { label: 'Highest Rated', sortBy: 'averageRating', order: 'desc' },
  { label: 'Lowest Rated', sortBy: 'averageRating', order: 'asc' },

  { label: 'Prep Time: Quickest', sortBy: 'prepTime', order: 'asc' },
  { label: 'Prep Time: Longest', sortBy: 'prepTime', order: 'desc' },

  { label: 'Name: A-Z', sortBy: 'name', order: 'asc' },
  { label: 'Name: Z-A', sortBy: 'name', order: 'desc' },
] as const;

const RecipeSort = ({ currentSort, onSortChange }: RecipeSortProps) => {
  const activeValue = `${currentSort.sortBy}_${currentSort.order}`;

  const handleChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const selected = SORT_OPTIONS.find(
      opt => `${opt.sortBy}_${opt.order}` === e.target.value
    );

    if (selected) {
      onSortChange({
        sortBy: selected.sortBy,
        order: selected.order,
      });
    }
  };

  return (
    <div className='relative flex items-center min-w-[200px]'>
      <ArrowsUpDownIcon className='absolute left-3 size-4 text-slate-400 pointer-events-none' />

      <select
        value={activeValue}
        onChange={handleChange}
        className='w-full appearance-none bg-white border border-slate-200 text-slate-700 py-2.5 pl-10 pr-10 
          rounded-xl focus:outline-none focus-within:ring-2 focus-within:ring-blue-500 focus-within:border-transparent 
          duration-100 cursor-pointer text-sm font-medium transition-all'
      >
        {SORT_OPTIONS.map(option => (
          <option
            key={`${option.sortBy}_${option.order}`}
            value={`${option.sortBy}_${option.order}`}
          >
            {option.label}
          </option>
        ))}
      </select>

      <ChevronDownIcon className='absolute right-3 size-4 text-slate-400 pointer-events-none' />
    </div>
  );
};

export default RecipeSort;
