import { useState } from 'react';
import ReviewItem from './ReviewItem';
import StarRating from '../StarRating';

type ReviewSectionProps = {
  recipeId: number;
  reviews: any[];
  reviewCount: number;
  isPending: boolean;
};

const ReviewSection = ({
  recipeId,
  reviews,
  reviewCount,
  isPending,
}: ReviewSectionProps) => {
  const [userRating, setUserRating] = useState(5);
  const [comment, setComment] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    console.log({ recipeId, userRating, comment });
    // Mutation logic goes here
    setComment('');
  };

  return (
    <section className='mt-12 border-t border-slate-100 pt-12'>
      <div className='grid grid-cols-1 lg:grid-cols-3 gap-12'>
        <div className='lg:col-span-1'>
          <div className='sticky top-24'>
            <h3 className='text-2xl font-bold text-slate-900 mb-2'>
              Rate this recipe
            </h3>
            <p className='text-slate-500 mb-6'>
              What did you think of the flavor and instructions?
            </p>

            <form
              onSubmit={handleSubmit}
              className='space-y-4 bg-slate-50 p-6 rounded-2xl border border-slate-100'
            >
              <div>
                <label className='block text-sm font-semibold text-slate-700 mb-3'>
                  Your Rating
                </label>
                <span className='flex items-center'>
                  <StarRating
                    onChange={val => setUserRating(val)}
                    rating={userRating}
                    realValue
                  />
                  <span className='ml-2 text-sm font-bold text-slate-400 self-center'>
                    {userRating}/5
                  </span>
                </span>
              </div>

              <div>
                <label className='block text-sm font-semibold text-slate-700 mb-2'>
                  Review Details
                </label>
                <textarea
                  value={comment}
                  onChange={e => setComment(e.target.value)}
                  className='w-full p-4 rounded-xl border border-slate-200 focus:ring-2 focus:ring-blue-500 
                    focus:border-transparent outline-none min-h-[140px] text-sm bg-white resize-none'
                  placeholder='Write your review here...'
                  required
                />
              </div>

              <button
                type='submit'
                className='w-full py-3.5 bg-blue-600 text-white font-bold rounded-xl hover:bg-blue-700 transition-all shadow-lg shadow-blue-100'
              >
                Submit Review
              </button>
            </form>
          </div>
        </div>

        {/* RIGHT: LIST */}
        <div className='lg:col-span-2'>
          <h3 className='text-2xl font-bold text-slate-900 mb-8 flex items-center gap-3'>
            Reviews
            <span className='px-2.5 py-0.5 rounded-full bg-slate-100 text-slate-500 text-sm font-medium'>
              {reviewCount}
            </span>
          </h3>

          {isPending ? (
            <div className='space-y-4 animate-pulse'>
              {[1, 2, 3].map(i => (
                <div key={i} className='h-32 bg-gray-100 rounded-xl' />
              ))}
            </div>
          ) : reviews.length === 0 ? (
            <div className='text-center py-16 bg-slate-50 rounded-2xl border-2 border-dashed border-slate-200'>
              <p className='text-slate-400'>
                No reviews yet. Be the first to share your experience!
              </p>
            </div>
          ) : (
            <div className='flex flex-col gap-6'>
              {reviews.map(review => (
                <ReviewItem key={review.id} review={review} />
              ))}
            </div>
          )}
        </div>
      </div>
    </section>
  );
};

export default ReviewSection;
