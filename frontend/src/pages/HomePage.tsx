import HeroSection from '../components/home/HeroSection';
import RecipeSection from '../components/home/RecipeSection';
import { useRecipes } from '../hooks/useRecipes';

const HomePage = () => {
  const { data: trendingRecipesPage, isPending: isPendingTrending } =
    useRecipes(
      { page: 0, size: 4, sort: { sortBy: 'averageRating', order: 'desc' } },
      {}
    );

  const { data: quickRecipesPage, isPending: isPendingQuick } = useRecipes(
    { page: 0, size: 4, sort: { sortBy: 'averageRating', order: 'desc' } },
    { maxPrepTime: 30 }
  );

  const { data: veganRecipesPage, isPending: isPendingVegan } = useRecipes(
    { page: 0, size: 4, sort: { sortBy: 'averageRating', order: 'desc' } },
    { tags: ['vegan'] }
  );

  return (
    <div className='min-h-screen bg-white'>
      <HeroSection />

      <div className='flex flex-col gap-4 mt-8 pb-20'>
        <RecipeSection
          title='Trending Now'
          subtitle='What the community is cooking this week'
          recipes={trendingRecipesPage?.content || []}
          viewAllLink='/trending'
        />

        <div className='max-w-7xl mx-auto px-6 w-full py-8'>
          <div
            className='relative overflow-hidden rounded-3xl bg-blue-600 px-8 py-12 md:px-16 md:flex items-center 
              justify-between shadow-xl shadow-slate-200'
          >
            <div className='relative z-10'>
              <h3 className='text-3xl font-bold text-white mb-3'>
                Share your secret recipe
              </h3>
              <p className='text-slate-300 max-w-lg text-lg'>
                Join our community of chefs and earn badges for your culinary
                creations.
              </p>
            </div>
            <button
              className='relative z-10 mt-8 md:mt-0 px-8 py-4 bg-slate-900 text-white font-bold cursor-pointer
                rounded-xl hover:bg-slate-800 transition-colors shadow-lg shadow-slate-900/20 whitespace-nowrap'
            >
              Create Recipe
            </button>
          </div>
        </div>

        <RecipeSection
          title='Under 30 Minutes'
          subtitle='Quick and easy meals for busy days'
          recipes={quickRecipesPage?.content || []}
        />

        <RecipeSection
          title='Vegan Favorites'
          recipes={veganRecipesPage?.content || []}
        />
      </div>
    </div>
  );
};

export default HomePage;
