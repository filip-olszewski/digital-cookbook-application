import { useState } from 'react';

type FadeInImageProps = {
  src: string;
  alt: string;
  className?: string;
};

const FadeInImage = ({ src, alt, className = '' }: FadeInImageProps) => {
  const [isLoaded, setIsLoaded] = useState(false);

  return (
    <div className={`relative overflow-hidden ${className}`}>
      {' '}
      {!isLoaded && (
        <div className='absolute inset-0 bg-gray-200 animate-pulse' />
      )}
      <img
        src={src}
        alt={alt}
        loading='lazy'
        onLoad={() => setIsLoaded(true)}
        className={`
          w-full h-full object-cover transition-opacity duration-300 ease-in-out
          ${isLoaded ? 'opacity-100' : 'opacity-0'}
        `}
      />
    </div>
  );
};

export default FadeInImage;
