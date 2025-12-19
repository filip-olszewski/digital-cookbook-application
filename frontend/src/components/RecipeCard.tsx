import { ClockIcon } from '@heroicons/react/24/outline';
import StarRating from './StarRating';
import FavouriteButton from './buttons/FavouriteButton';
import type { RecipeSummaryResponse } from '../api/generated';
import { Link } from 'react-router';
import { getOptimizedImageUrl } from '../utils/imageUtils';

type RecipeCardProps = {
  recipe: RecipeSummaryResponse;
};

const RecipeCard = ({ recipe }: RecipeCardProps) => {
  const authorName = recipe.authorName || 'Unknown';
  const initial = authorName.charAt(0) || '?';

  return (
    <div
      className='group relative flex flex-col bg-white rounded-2xl border border-slate-200 overflow-hidden 
        transition-all duration-300 shadow-slate-200 hover:shadow-xl hover:-translate-y-1'
    >
      <div className='relative aspect-[4/3] w-full overflow-hidden bg-gray-100'>
        <img
          src={getOptimizedImageUrl(recipe.imgUrl, 400)}
          alt=''
          className='h-full w-full object-cover transition-transform duration-700 group-hover:scale-105'
        />
        <span className='absolute top-3 left-3 bg-white/90 backdrop-blur-sm px-3 py-1 text-xs font-bold uppercase tracking-wider text-slate-700 rounded-full shadow-sm'>
          {recipe.category}
        </span>

        <div className='absolute top-0 right-0 z-10'>
          <FavouriteButton />
        </div>
      </div>

      <div className='flex flex-col flex-1 p-5'>
        <div className='mb-3'>
          <h3 className='text-lg font-bold text-slate-900 leading-tight group-hover:text-blue-600 transition-colors line-clamp-2'>
            <Link to={`/recipes/${recipe.slug}`}>
              <span className='absolute inset-0' aria-hidden='true' />
              {recipe.name}
            </Link>
          </h3>
        </div>

        <div className='flex items-center gap-4 text-slate-500 text-sm mb-4'>
          <div className='flex items-center gap-1.5'>
            <ClockIcon className='size-4' />
            <span>{recipe.prepTime} min</span>
          </div>
          <div className='w-px h-3 bg-slate-300' />
          <div className='flex items-center gap-1.5'>
            <StarRating rating={recipe.rating} size='sm' />
            <span className='text-xs font-medium'>({recipe.reviewCount})</span>
          </div>
        </div>

        <div className='flex flex-wrap gap-2 mb-4 mt-auto'>
          {recipe.tags.slice(0, 3).map(tag => (
            <span
              key={tag}
              className='px-2.5 py-1 text-xs font-medium text-slate-600 bg-slate-100 rounded-md'
            >
              {tag}
            </span>
          ))}
          {recipe.tags.length > 3 && (
            <span className='px-2 py-1 text-xs text-slate-400'>
              +{recipe.tags.length - 3}
            </span>
          )}
        </div>

        <div className='pt-4 mt-auto border-t border-slate-100 flex items-center justify-between'>
          <div className='flex items-center gap-2'>
            <div className='size-6 rounded-full bg-blue-100 flex items-center justify-center text-xs font-bold text-blue-600'>
              {initial}
            </div>
            <span className='text-sm font-medium text-slate-600 truncate max-w-[150px]'>
              {authorName}
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RecipeCard;
