import { Link, useParams } from 'react-router';
import { useRecipe } from '../hooks/useRecipe';
import { useReviews } from '../hooks/useReviews';
import { ClockIcon, UserIcon, FireIcon } from '@heroicons/react/24/outline';
import StarRating from '../components/StarRating';
import StepItem from '../components/StepItem';
import GoBackButton from '../components/buttons/GoBackButton';
import { getOptimizedImageUrl } from '../utils/imageUtils';
import FadeInImage from '../components/ui/FadeInImage';
import ReviewSection from '../components/reviews/ReviewSection';

const RecipePage = () => {
  const { slug } = useParams<{ slug: string }>();

  const {
    data: recipe,
    error: errorRecipe,
    isPending: isPendingRecipe,
  } = useRecipe(slug);

  const { data: reviewsPage, isPending: isPendingReviews } = useReviews(slug, {
    page: 0,
    size: 5,
    sort: { sortBy: 'createdAt', order: 'desc' },
  });

  const reviews = reviewsPage?.content ?? [];

  if (isPendingRecipe) {
    return (
      <div className='max-w-4xl mx-auto pt-40 px-6 min-h-screen animate-pulse'>
        <div className='h-8 bg-gray-200 rounded w-3/4 mb-4'></div>
        <div className='h-4 bg-gray-200 rounded w-1/4 mb-8'></div>
        <div className='w-full h-96 bg-gray-200 rounded-xl mb-8'></div>
        <div className='grid grid-cols-1 md:grid-cols-3 gap-8'>
          <div className='h-64 bg-gray-200 rounded col-span-2'></div>
          <div className='h-64 bg-gray-200 rounded'></div>
        </div>
      </div>
    );
  }

  if (errorRecipe) {
    return (
      <div className='max-w-4xl mx-auto pt-40 px-6 text-center text-red-500'>
        <h2 className='text-2xl font-bold mb-2'>Error loading recipe</h2>
        <p>{errorRecipe.message}</p>
      </div>
    );
  }

  if (!recipe) {
    return (
      <div className='max-w-4xl mx-auto pt-40 px-6 text-center'>
        <h1 className='text-3xl font-bold text-gray-800'>Recipe not found</h1>
      </div>
    );
  }

  return (
    <div className='max-w-5xl mx-auto pt-32 pb-20 px-6'>
      <div className='mb-8'>
        <GoBackButton />
      </div>

      <div className='mb-8 text-center md:text-left'>
        <div className='flex justify-center md:justify-start mb-4'>
          <span className='inline-block px-3 py-1 rounded-full bg-blue-100 text-blue-700 text-xs font-bold uppercase tracking-wide'>
            {recipe.category.name}
          </span>
        </div>

        <h1 className='text-4xl md:text-5xl font-extrabold text-slate-900 mb-4 tracking-tight'>
          {recipe.name}
        </h1>

        <div className='flex items-center justify-center md:justify-start gap-3 mb-6'>
          <div className='size-8 rounded-full bg-slate-200 flex items-center justify-center text-slate-600 font-bold text-xs'>
            {recipe.author.firstName.charAt(0)}
          </div>
          <span className='font-medium text-slate-500'>
            by
            <Link
              to={`/users/${recipe.author.username}`}
              className='font-semibold text-blue-500 underline ml-1'
            >
              {recipe.author.firstName} {recipe.author.lastName}
            </Link>
          </span>
        </div>

        <p className='text-lg text-slate-600 max-w-2xl mx-auto md:mx-0 leading-relaxed'>
          {recipe.description}
        </p>

        <div className='flex flex-wrap items-center justify-center md:justify-start gap-6 mt-6 text-slate-700 font-medium'>
          <div className='flex items-center gap-2'>
            <ClockIcon className='size-6 text-slate-400' />
            <span>{recipe.prepTime} min</span>
          </div>
          <div className='w-px h-6 bg-slate-200 hidden md:block'></div>
          <div className='flex items-center gap-2'>
            <UserIcon className='size-6 text-slate-400' />
            <span>{recipe.servings} Servings</span>
          </div>
          <div className='w-px h-6 bg-slate-200 hidden md:block'></div>
          <div className='flex items-center gap-2'>
            <StarRating rating={recipe.rating} />
            <span className='text-sm text-slate-500'>
              ({recipe.reviewCount} reviews)
            </span>
          </div>
        </div>
      </div>

      <div className='w-full aspect-video md:aspect-[21/9] rounded-2xl overflow-hidden shadow-lg mb-12 bg-gray-100'>
        <FadeInImage
          src={getOptimizedImageUrl(recipe.imgUrl, 1200)}
          alt={recipe.name}
          className='w-full aspect-video md:aspect-[21/9] rounded-2xl shadow-lg mb-12 bg-gray-100'
        />
      </div>

      <div className='grid grid-cols-1 lg:grid-cols-3 gap-12'>
        <div className='lg:col-span-1 space-y-8'>
          <div className='bg-slate-50 p-6 rounded-2xl border border-slate-100'>
            <h3 className='text-xl font-bold text-slate-900 mb-4'>
              Ingredients
            </h3>
            <ul className='space-y-3'>
              {recipe.ingredients.map((item, idx) => (
                <li key={idx} className='flex items-start gap-3 text-slate-700'>
                  <div className='mt-1.5 size-1.5 rounded-full bg-blue-500 shrink-0'></div>
                  <span className='leading-snug'>
                    <span className='font-semibold'>
                      {item.amount} {item.unit}
                    </span>{' '}
                    {item.name}
                  </span>
                </li>
              ))}
            </ul>
          </div>

          <div className='flex flex-wrap gap-2'>
            {recipe.tags.map(tag => (
              <span
                key={tag.slug}
                className='px-3 py-1 bg-slate-100 text-slate-600 text-sm font-medium rounded-full'
              >
                #{tag.label}
              </span>
            ))}
          </div>
        </div>

        <div className='lg:col-span-2 space-y-12'>
          <section>
            <h3 className='text-2xl font-bold text-slate-900 mb-6'>
              Instructions
            </h3>
            <div className='space-y-4'>
              {recipe.steps
                .sort((a, b) => a.stepOrder - b.stepOrder)
                .map(step => (
                  <StepItem key={step.id} step={step} />
                ))}
            </div>
          </section>
        </div>
      </div>

      <hr className='border-slate-100 my-10' />

      <ReviewSection
        recipeId={recipe.id}
        reviews={reviews}
        reviewCount={recipe.reviewCount}
        isPending={isPendingReviews}
      />
    </div>
  );
};

export default RecipePage;
