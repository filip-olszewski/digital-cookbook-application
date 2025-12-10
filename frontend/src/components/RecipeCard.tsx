import React from 'react';
import { Link } from 'react-router';
import type { RecipeSummary } from '../types/recipeTypes';
import {
  ClockIcon,
  HeartIcon,
} from '@heroicons/react/24/outline';
import StarRating from './StarRating';
import FavouriteButton from './buttons/FavouriteButton';

type RecipeCardProps = {
  recipe: RecipeSummary;
};

const RecipeCard = ({ recipe }: RecipeCardProps) => {
  return (
    <Link
      to={`/recipes/${recipe.slug}`}
      className='rounded-2xl overflow-hidden relative duration-300 
      hover:opacity-100 hover:saturate-100 group-hover:opacity-50 group-hover:saturate-70'
    >
      <FavouriteButton />
      <div className='aspect-video w-full rounded-2xl overflow-hidden'>
        <img
          src={recipe.imgUrl}
          alt=''
          className='aspect-video object-cover w-full duration-500 hover:scale-103'
        />
      </div>
      <div className='px-2 py-4'>
        <div>
          <h5 className='text-sm font-semibold text-blue-500'>
            {recipe.category}
          </h5>
          <h3>{recipe.name}</h3>
        </div>
        <div className='flex gap-6 items-center just'>
          <p className='text-slate-500 flex items-center gap-1 mt-1'>
            <ClockIcon className='size-5' />
            <span>{recipe.prepTime} minutes</span>
          </p>
          <StarRating rating={recipe.rating} />
        </div>
      </div>
      <div className='px-2 flex gap-1'>
        {recipe.tags.map(tag => (
          <span
            className='py-1 px-2 bg-blue-400 text-sm font-semibold text-white 
          rounded-md duration-200 hover:bg-blue-300'
          >
            {tag}
          </span>
        ))}
      </div>
      <div className='px-2 py-4'>
        <h4 className='text-slate-500'>{recipe.authorName}</h4>
      </div>
    </Link>
  );
};

export default RecipeCard;
