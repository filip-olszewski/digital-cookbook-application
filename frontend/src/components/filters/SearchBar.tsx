import { MagnifyingGlassIcon } from '@heroicons/react/24/outline';
import { XMarkIcon } from '@heroicons/react/24/solid';
import React, { useRef, type FormEvent, type RefObject } from 'react';

type SearchBarProps = {
  value: string;
  onChange: (value: string) => void;
};

const SearchBar = ({ value, onChange }: SearchBarProps) => {
  return (
    <form
      className='flex items-center w-full rounded-xl border border-slate-200 px-3 py-2
     bg-white focus-within:ring-2 focus-within:ring-blue-500 focus-within:border-transparent transition-all'
      onSubmit={e => {
        e.preventDefault();
        onChange('');
      }}
    >
      <MagnifyingGlassIcon className='size-5 text-slate-400 mr-2' />
      <input
        type='text'
        className='flex-1 bg-transparent outline-none text-slate-700 placeholder:text-slate-400'
        placeholder='Search recipes...'
        value={value}
        onChange={e => onChange(e.target.value)}
      />
      <button type='submit' className='cursor-pointer'>
        <XMarkIcon className='text-slate-400 size-5' />
      </button>
    </form>
  );
};

export default SearchBar;
