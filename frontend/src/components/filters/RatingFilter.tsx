import StarRating from '../StarRating';

type RatingFilterProps = {
  minRating?: number;
  onChange: (rating: number) => void;
};

const RatingFilter = ({ minRating = 0, onChange }: RatingFilterProps) => {
  return (
    <div>
      <h3 className='font-semibold text-gray-900 mb-4'>Minimum Rating</h3>
      <div className='flex items-center justify-between'>
        <StarRating rating={minRating} onChange={onChange} size='lg' />
        <span className='text-sm text-gray-400'>
          {minRating > 0 ? `& Up` : 'Any'}
        </span>
      </div>
    </div>
  );
};

export default RatingFilter;
