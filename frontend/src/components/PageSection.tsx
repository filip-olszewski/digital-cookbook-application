import React from 'react';
import RecipeCard from './RecipeCard';
import { Link } from 'react-router';

type PageSectionProps = {
  title: string;
  type?: 'list' | 'grid';
  limit?: number;
  recipes: any[];
};

const PageSection = ({
  title,
  type = 'list',
  limit = 4,
  recipes,
}: PageSectionProps) => {
  recipes = recipes.slice(0, limit);

  return (
    <section className='flex flex-col gap-8 mb-16'>
      <h1>{title}</h1>
      <div className='grid grid-cols-4 gap-4 h-full flex-1 min-h-0'>
        {recipes.map((recipe, key) => (
          <RecipeCard recipe={recipe} key={key} />
        ))}
      </div>
    </section>
  );
};

export default PageSection;
