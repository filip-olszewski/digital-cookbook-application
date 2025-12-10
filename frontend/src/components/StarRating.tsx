import { StarIcon as StarIconSolid } from '@heroicons/react/24/solid';
import { StarIcon as StarIconOutline } from '@heroicons/react/24/outline';

type StarRatingProps = {
    rating: number
    size?: "sm" | "md" | "lg"
}

const MAX_RATING = 5;

const StarRating = ({ rating, size = 'md' }: StarRatingProps) => {

  const rounded = Math.round(rating);
  const sizes = {
    sm: 'size-4',
    md: 'size-5',
    lg: 'size-6'
  }
  
  return (
    <div className='text-amber-400 flex items-center'>
      {
        [...Array(MAX_RATING)].map((_, i) => {
          const filled = rounded > i;
          return filled ? <StarIconSolid key={i} className={sizes[size]} />
            : <StarIconOutline key={i} className={sizes[size]} />
        })
      }
    </div>
  )
}

export default StarRating
