import { useEffect, useState } from 'react';
import { createPortal } from 'react-dom';
import { XMarkIcon } from '@heroicons/react/24/outline';

type ModalProps = {
  isOpen: boolean;
  onClose: () => void;
  children: React.ReactNode;
  maxWidth?: 'sm' | 'md' | 'lg' | 'xl';
};

const Modal = ({ isOpen, onClose, children, maxWidth = 'md' }: ModalProps) => {
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    if (isOpen) {
      setMounted(true);
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'unset';
    }
  }, [isOpen]);

  if (!isOpen || !mounted) return null;

  const maxWidthClass = {
    sm: 'max-w-sm',
    md: 'max-w-md',
    lg: 'max-w-lg',
    xl: 'max-w-xl',
  }[maxWidth];

  return createPortal(
    <div className='fixed inset-0 z-[200] flex items-center justify-center p-4'>
      <div
        className='absolute inset-0 bg-slate-900/40 backdrop-blur-[2px] animate-fade-in transition-all'
        onClick={onClose}
      />

      <div
        className={`relative w-full ${maxWidthClass} bg-white rounded-[2rem] shadow-2xl overflow-hidden animate-modal-appear border border-slate-100`}
      >
        <button
          onClick={onClose}
          className='absolute top-5 right-5 p-2 text-slate-400 hover:text-slate-600 hover:bg-slate-100 rounded-full transition-all active:scale-90 z-10'
        >
          <XMarkIcon className='size-5 stroke-[2.5]' />
        </button>

        {children}
      </div>
    </div>,
    document.body
  );
};

export default Modal;
