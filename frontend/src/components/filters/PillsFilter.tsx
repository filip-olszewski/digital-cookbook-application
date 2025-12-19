type PillsFilterProps<T> = {
  title: string;
  options: T[];
  getLabel: (option: T) => string;
  getValue: (option: T) => string;
  data?: string[];
  onChange: (newValues: string[]) => void;
  color?: 'amber' | 'emerald';
};

const PillsFilter = <T,>({
  title,
  options,
  getLabel,
  getValue,
  data = [],
  onChange,
  color = 'amber',
}: PillsFilterProps<T>) => {
  const toggleOption = (value: string) => {
    const newValues = data.includes(value)
      ? data.filter(item => item !== value)
      : [...data, value];
    onChange(newValues);
  };

  const activeStyles =
    color === 'amber'
      ? 'bg-amber-100 border-amber-200 text-amber-800'
      : 'bg-emerald-100 border-emerald-200 text-emerald-800';

  return (
    <div>
      <h3 className='font-semibold text-gray-900 mb-4'>{title}</h3>
      <div className='flex flex-wrap gap-2'>
        {options.map(opt => {
          const value = getValue(opt);
          const label = getLabel(opt);
          const isActive = data.includes(value);

          return (
            <button
              key={value}
              type='button'
              onClick={() => toggleOption(value)}
              className={`
                px-3 py-1.5 text-sm rounded-full transition-colors border font-medium
                ${
                  isActive
                    ? activeStyles
                    : 'bg-white border-gray-200 text-gray-600 hover:border-gray-300'
                }
              `}
            >
              {label}
            </button>
          );
        })}
      </div>
    </div>
  );
};

export default PillsFilter;
