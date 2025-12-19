import { useState } from 'react';
import { CheckCircleIcon } from '@heroicons/react/24/solid'; // Solid icon for done state
import { CheckCircleIcon as CheckCircleOutline } from '@heroicons/react/24/outline'; // Outline for todo state
import type { StepResponse } from '../api/generated';

const StepItem = ({ step }: { step: StepResponse }) => {
  const [isCompleted, setIsCompleted] = useState(false);

  return (
    <div
      onClick={() => setIsCompleted(!isCompleted)}
      className={`
        relative flex gap-4 p-6 rounded-xl border transition-all duration-300 cursor-pointer group
        ${
          isCompleted
            ? 'bg-slate-50 border-slate-100'
            : 'bg-white border-slate-200 shadow-sm hover:border-blue-300 hover:shadow-md'
        }
      `}
    >
      <div className='flex-shrink-0'>
        <div
          className={`
          size-8 rounded-full flex items-center justify-center font-bold text-sm transition-colors duration-300
          ${
            isCompleted
              ? 'bg-green-100 text-green-600'
              : 'bg-slate-900 text-white group-hover:bg-blue-600'
          }
        `}
        >
          {isCompleted ? (
            <CheckCircleIcon className='size-6' />
          ) : (
            <span>{step.stepOrder}</span>
          )}
        </div>
      </div>

      <div
        className={`flex-1 transition-opacity duration-300 ${
          isCompleted ? 'opacity-50 grayscale' : 'opacity-100'
        }`}
      >
        <p
          className={`text-lg leading-relaxed whitespace-pre-line ${
            isCompleted
              ? 'text-slate-500 line-through decoration-slate-300'
              : 'text-slate-700'
          }`}
        >
          {step.instructions}
        </p>

        {step.imgUrl && (
          <img
            src={step.imgUrl}
            alt={`Step ${step.stepOrder}`}
            className='mt-4 rounded-lg w-full max-w-md shadow-sm'
          />
        )}
      </div>

      <div className='absolute top-4 right-4 opacity-0 group-hover:opacity-100 transition-opacity'>
        {!isCompleted && (
          <CheckCircleOutline className='size-6 text-slate-300' />
        )}
      </div>
    </div>
  );
};

export default StepItem;
