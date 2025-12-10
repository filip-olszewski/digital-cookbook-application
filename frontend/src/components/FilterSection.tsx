import { ChevronDownIcon, ChevronUpIcon } from '@heroicons/react/24/solid';
import { useState, type ReactNode } from 'react'

type FilterSectionProps = {
  children: ReactNode;
  label: string;
  defaultOpen?: boolean;
}

const FilterSection = ({ children, label, defaultOpen = true }: FilterSectionProps) => {

  const [isOpen, setIsOpen] = useState<boolean>(defaultOpen);

  return (
    <div className='bg-white p-2 rounded-xl'>
      <button 
        className='flex items-center gap-2' 
        onClick={() => setIsOpen(prev => !prev)}
      >
        <h5>{label}</h5>
        {
          isOpen ? (
            <ChevronUpIcon className='size-4' />
          ) : (
            <ChevronDownIcon className='size-4' />
          )
        }
      </button>
      <div 
        className={`grid transition-[grid-template-rows] duration-300 ease-in-out ${
          isOpen ? 'grid-rows-[1fr] opacity-100' : 'grid-rows-[0fr] opacity-0'
        }`}
      >
        <div className="overflow-hidden">
          <div className="pt-2">
            {children}
          </div>
        </div>
      </div>
    </div>
  )
}

export default FilterSection
