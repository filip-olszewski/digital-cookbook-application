import RecipeCard from './RecipeCard';

type RecipeRowProps = {
  title: string;
  limit?: number;
  recipes: any[];
};

const RecipeRow = ({
  title,
  limit = 4,
  recipes,
}: RecipeRowProps) => {
  
  recipes = recipes.slice(0, limit);

  return (
    <section className='flex flex-col gap-8 mb-16'>
      <h1>{title}</h1>
      <div className='grid grid-cols-4 gap-4 h-full flex-1 min-h-0 group'>
        {recipes.map((recipe, key) => (
          <RecipeCard recipe={recipe} key={key} />
        ))}
      </div>
    </section>
  );
};

export default RecipeRow;
