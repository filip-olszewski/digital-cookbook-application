import { HeartIcon } from '@heroicons/react/24/outline'
import React from 'react'

const FavouriteButton = () => {
  return (
    <button 
      className='absolute top-4 right-4 w-10 h-10 rounded-full 
      bg-white grid place-content-center z-10 duration-300 hover:text-pink-500'
    >
      <HeartIcon className='size-6' />
    </button>
  )
}

export default FavouriteButton
