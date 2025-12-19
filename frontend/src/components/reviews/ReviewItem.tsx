import StarRating from '../StarRating';

type ReviewItemProps = {
  review: any;
};

const ReviewItem = ({ review }: ReviewItemProps) => {
  return (
    <div
      className='bg-white p-6 rounded-xl border border-slate-100 shadow-md transition-all 
      hover:shadow-xl shadow-slate-100'
    >
      <div className='flex items-center justify-between mb-4'>
        <div className='flex items-center gap-3'>
          <div className='size-10 rounded-full bg-blue-50 text-blue-600 flex items-center justify-center font-bold border border-blue-100'>
            {review.user.firstName.charAt(0)}
          </div>
          <div>
            <span className='block font-bold text-slate-900'>
              {review.user.firstName}
            </span>
            <span className='text-xs text-slate-400'>
              {new Date(review.postedAt).toLocaleDateString()}
            </span>
          </div>
        </div>
        <StarRating rating={review.rating} size='sm' />
      </div>
      <p className='text-slate-600 leading-relaxed text-sm md:text-base'>
        {review.comment}
      </p>
    </div>
  );
};

export default ReviewItem;
