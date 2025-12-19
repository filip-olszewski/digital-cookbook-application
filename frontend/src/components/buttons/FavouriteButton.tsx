import { useState } from 'react';
import AuthGateModal from '../auth/AuthGateModal';
import { HeartIcon } from '@heroicons/react/24/solid';

const FavouriteButton = () => {
  const [showAuthModal, setShowAuthModal] = useState(false);
  const isAuthenticated = false;

  const handleFavoriteClick = (e: React.MouseEvent) => {
    e.preventDefault();
    if (!isAuthenticated) {
      setShowAuthModal(true);
      return;
    }

    console.log('Added to favorites!');
  };

  return (
    <>
      <button
        className='absolute top-3 right-3 w-8 h-8 rounded-full text-slate-400 cursor-pointer
      bg-white grid place-content-center z-10 duration-300 hover:text-pink-500'
        onClick={handleFavoriteClick}
      >
        <HeartIcon className='size-5' />
      </button>

      <AuthGateModal
        isOpen={showAuthModal}
        onClose={() => setShowAuthModal(false)}
      />
    </>
  );
};

export default FavouriteButton;
