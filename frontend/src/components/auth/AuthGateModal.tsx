import Modal from '../ui/Modal';
import { LockClosedIcon } from '@heroicons/react/24/outline';
import { NavLink } from 'react-router';

type AuthGateModalProps = {
  isOpen: boolean;
  onClose: () => void;
};

const AuthGateModal = ({ isOpen, onClose }: AuthGateModalProps) => {
  return (
    <Modal isOpen={isOpen} onClose={onClose} maxWidth='sm'>
      <div className='p-8 text-center'>
        <div className='mx-auto size-16 bg-blue-50 rounded-2xl flex items-center justify-center mb-6'>
          <LockClosedIcon className='size-8 text-blue-600' />
        </div>

        <h2 className='text-2xl font-bold text-slate-900 mb-3'>
          Login Required
        </h2>
        <p className='text-slate-500 mb-8'>
          Join our community to save recipes and interact with other chefs.
        </p>

        <div className='flex flex-col gap-3'>
          <NavLink
            to='/login'
            className='w-full py-3.5 bg-blue-600 text-white font-bold rounded-xl hover:bg-blue-700 shadow-lg shadow-blue-100'
          >
            Log In
          </NavLink>
          <NavLink
            to='/signup'
            className='w-full py-3.5 bg-white text-slate-700 font-bold rounded-xl border border-slate-200 hover:bg-slate-50'
          >
            Create an Account
          </NavLink>
        </div>
      </div>
    </Modal>
  );
};

export default AuthGateModal;
