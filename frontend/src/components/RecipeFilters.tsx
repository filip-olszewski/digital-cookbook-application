import React from 'react';
import FilterSection from './FilterSection';

const RecipeFilters = () => {
  return (
    <div className='w-full h-96 bg-slate-100 rounded-lg px-2'>
      <h4 className='px-2 py-4'>Filter</h4>
      <div className='flex flex-col gap-1'>
        <FilterSection label="Category">
          <fieldset>
            <span className='flex gap-1'>
              <label>Dinner</label>
              <input type="checkbox" name="Dinner" id="1" />
            </span>
            <span className='flex gap-1'>
              <label>Breakfast</label>
              <input type="checkbox" name="Dinner" id="1" />
            </span>
            <span className='flex gap-1'>
              <label>Winter</label>
              <input type="checkbox" name="Dinner" id="1" />
            </span>
          </fieldset>
        </FilterSection>
        <FilterSection label="Prep Time">
          a
        </FilterSection>
      </div>
    </div>
  );
};

export default RecipeFilters;
