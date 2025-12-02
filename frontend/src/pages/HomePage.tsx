import React from 'react';
import PageSection from '../components/PageSection';
import { recipes } from '../mock/MockRecipes';

const HomePage = () => {
  return (
    <div className='px-48 pt-40'>
      <PageSection title='Popular recipes' recipes={recipes} />
      <PageSection title='Trending' recipes={recipes} />
    </div>
  );
};

export default HomePage;
