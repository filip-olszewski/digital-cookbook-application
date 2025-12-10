import React, { useEffect } from 'react';
import { fetchRecipes } from '../api/recipeApi';
import { useRecipes } from '../hooks/useRecipes';
import RecipeCard from '../components/RecipeCard';
import type { RecipeSummary } from '../types/recipeTypes';
import RecipeFilters from '../components/RecipeFilters';

const BrowseRecipesPage = () => {
  const { data, error, isPending } = useRecipes();

  if (error) {
    return (
      <div>
        <h1>Error!</h1>
        <h2>{error.message}</h2>
      </div>
    );
  }

  if (isPending) {
    return (
      <div>
        <h1>Loading...</h1>
      </div>
    );
  }

  const recipes: RecipeSummary[] = data.content || [];

  return (
    <div className='px-48 pt-40'>
      <h1 className='mb-8'>Browse recipes</h1>
      <div className='flex gap-8'>
        <div className='flex-1'>
          <RecipeFilters />
        </div>
        <div className='flex-4 grid grid-cols-4 gap-4'>
          {recipes.map(recipe => (
            <RecipeCard key={recipe.id} recipe={recipe} />
          ))}
        </div>
      </div>
    </div>
  );
};

export default BrowseRecipesPage;
