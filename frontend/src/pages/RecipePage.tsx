import React from 'react'
import { useParams } from 'react-router'
import { useRecipe } from '../hooks/useRecipe';

const RecipePage = () => {

  const params = useParams();
  const { data: recipe, error , isPending } = useRecipe(params.slug);

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

  console.log(recipe)

  return (
    <div>
      
    </div>
  )
}

export default RecipePage
