import { StarIcon as StarIconSolid } from '@heroicons/react/24/solid';
import { StarIcon as StarIconOutline } from '@heroicons/react/24/outline';

type StarRatingProps = {
  rating?: number;
  size?: 'sm' | 'md' | 'lg';
  onChange?: (newRating: number) => void;
  realValue?: boolean;
};

const MAX_RATING = 5;

const StarRating = ({
  rating = 0,
  size = 'md',
  onChange,
  realValue = false,
}: StarRatingProps) => {
  const rounded = Math.round(rating);

  const sizes = {
    sm: 'size-4',
    md: 'size-5',
    lg: 'size-7',
  };

  const handleStarClick = (starIndex: number) => {
    if (!onChange) return;

    const starValue = starIndex + 1;
    const valueToSend = starValue === 5 ? (realValue ? 5 : 4.5) : starValue;

    if (valueToSend === rating) {
      onChange(0);
    } else {
      onChange(valueToSend);
    }
  };

  return (
    <div className='flex items-center gap-1'>
      <span className='flex items-center'>
        {[...Array(MAX_RATING)].map((_, i) => {
          const starValue = i + 1;
          const filled = rounded >= starValue;

          return (
            <button
              key={i}
              type='button'
              disabled={!onChange}
              onClick={() => handleStarClick(i)}
              className={`
              ${
                onChange
                  ? 'cursor-pointer transition-transform'
                  : 'cursor-default'
              }
              text-amber-400 focus:outline-none hover:text-amber-300
            `}
            >
              {filled ? (
                <StarIconSolid className={sizes[size]} />
              ) : (
                <StarIconOutline className={sizes[size]} />
              )}
            </button>
          );
        })}
      </span>
    </div>
  );
};

export default StarRating;
