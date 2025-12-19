import React from 'react';

type SliderFilterProps = {
  selectedMaxTime?: number;
  onChange: (time: number | undefined) => void;
  sliderMaxValue: number;
};

const SLIDER_STEP = 5;

const SliderFilter = ({
  selectedMaxTime,
  onChange,
  sliderMaxValue,
}: SliderFilterProps) => {
  const sliderValue = selectedMaxTime ?? sliderMaxValue;

  const handleSliderChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const val = Number(e.target.value);
    onChange(val === sliderMaxValue ? undefined : val);
  };

  return (
    <div>
      <h3 className='font-semibold text-gray-900 mb-4'>Max Prep Time</h3>
      <div className='flex justify-between text-sm text-gray-500 mb-2'>
        <span>0m</span>
        <span className='font-medium text-amber-600'>
          {selectedMaxTime === undefined || selectedMaxTime === sliderMaxValue
            ? 'Any time'
            : `< ${selectedMaxTime} mins`}
        </span>
      </div>
      <input
        type='range'
        min={0}
        max={sliderMaxValue}
        step={SLIDER_STEP}
        value={sliderValue}
        onChange={handleSliderChange}
        className='w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer accent-amber-500'
      />
    </div>
  );
};

export default SliderFilter;
