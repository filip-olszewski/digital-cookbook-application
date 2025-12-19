import { ArrowLeftIcon } from '@heroicons/react/24/outline';
import { useNavigate } from 'react-router';

type GoBackButtonProps = {
  label?: string;
  to?: string;
};

const GoBackButton = ({ label = 'Go back', to }: GoBackButtonProps) => {
  const navigate = useNavigate();

  return (
    <button
      onClick={() => {
        to ? navigate(to) : navigate(-1);
      }}
      className='cursor-pointer group inline-flex items-center gap-2 px-5 py-2.5 bg-slate-900 hover:bg-slate-700
       text-white rounded-full transition-all duration-200 shadow-sm hover:shadow-md text-sm font-bold'
    >
      <ArrowLeftIcon className='size-5 transition-transform duration-200 group-hover:-translate-x-1' />
      {label}
    </button>
  );
};

export default GoBackButton;
