import React from 'react';
import { Link } from 'react-router';

type RecipeCardProps = {
  recipe: any;
};

const RecipeCard = ({ recipe }: RecipeCardProps) => {
  return (
    <div
      className='relative rounded-lg overflow-hidden p-4 bg-white
     shadow-2xl shadow-blue-100 border-1 border-blue-100 cursor-pointer'
    >
      <Link to={`/recipes/${recipe.slug}`} className='inset-0 absolute'></Link>

      <img
        src={recipe.imgUrl}
        alt=''
        className='h-2/3 w-full object-cover rounded-md'
      />
      <div className='h-1/3 flex flex-col'>
        <h4 className='font-medium mt-4'>{recipe.name}</h4>
      </div>
    </div>
  );
};

export default RecipeCard;
