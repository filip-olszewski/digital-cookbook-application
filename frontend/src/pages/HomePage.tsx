import React from 'react';
import RecipeRow from '../components/RecipeRow';
import { useRecipes } from '../hooks/useRecipes';
import type { RecipeSummary } from '../types/recipeTypes';
import { recipes } from '../mock/MockRecipes';

const HomePage = () => {
  /* 
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
    const recipes: RecipeSummary[] = data.content;
  */


  return (
    <div className='px-48 pt-40'>
      <RecipeRow title='Popular recipes' recipes={recipes} />
      <RecipeRow title='Trending' recipes={recipes} />
    </div>
  );
};

export default HomePage;
