import { ArrowRightIcon } from '@heroicons/react/24/outline';
import { NavLink } from 'react-router';
import RecipeCard from '../RecipeCard';
import type { RecipeSummaryResponse } from '../../api/generated';

type RecipeSectionProps = {
  title: string;
  subtitle?: string;
  recipes: RecipeSummaryResponse[];
  viewAllLink?: string;
};

const RecipeSection = ({
  title,
  subtitle,
  recipes,
  viewAllLink = '/recipes',
}: RecipeSectionProps) => {
  if (!recipes || recipes.length === 0) return null;

  const displayRecipes = recipes.slice(0, 4);

  return (
    <section className='py-12 max-w-7xl mx-auto px-6'>
      <div className='flex items-end justify-between mb-8'>
        <div>
          <h2 className='text-3xl font-bold text-slate-900 tracking-tight'>
            {title}
          </h2>
          {subtitle && (
            <p className='text-slate-500 mt-2 text-lg'>{subtitle}</p>
          )}
        </div>

        <NavLink
          to={viewAllLink}
          className='flex items-center gap-1 text-sm font-semibold text-blue-500 hover:text-blue-700 transition-colors'
        >
          View all
          <ArrowRightIcon className='size-4' />
        </NavLink>
      </div>

      <div className='grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6'>
        {displayRecipes.map(recipe => (
          <div
            key={recipe.id}
            className='transition-transform hover:-translate-y-1 duration-300'
          >
            <RecipeCard recipe={recipe} />
          </div>
        ))}
      </div>
    </section>
  );
};

export default RecipeSection;
