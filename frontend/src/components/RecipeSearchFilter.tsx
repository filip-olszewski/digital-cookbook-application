import React from 'react';
import Filter from './Filter';

const RecipeSearchFilter = () => {
  return (
    <div className='w-full h-96 bg-slate-100 rounded-lg px-2'>
      <h4 className='px-2 py-4'>Filter</h4>
      <div className='flex flex-col gap-2'>
        <Filter />
        <Filter />
        <Filter />
      </div>
    </div>
  );
};

export default RecipeSearchFilter;
