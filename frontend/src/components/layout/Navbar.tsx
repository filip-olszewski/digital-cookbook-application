import { NavLink } from 'react-router'; // or 'react-router-dom'
import { PlusIcon } from '@heroicons/react/24/outline'; // Assuming you have heroicons

const Navbar = () => {
  const isLinkActive = ({ isActive }: { isActive: boolean }) => {
    return `text-sm font-medium transition-all duration-200 hover:text-slate-900 ${
      isActive
        ? 'text-slate-900 font-semibold bg-white shadow-sm px-4 py-1.5 rounded-full'
        : 'text-slate-500 px-4 py-1.5 hover:bg-white/50 rounded-full'
    }`;
  };

  return (
    <nav className='fixed top-0 left-0 right-0 z-50 bg-white/80 backdrop-blur-md border-b border-slate-100'>
      <div className='max-w-7xl mx-auto px-6 h-20 grid grid-cols-[1fr_auto_1fr] items-center'>
        <div className='flex justify-start'>
          <NavLink to='/' className='flex items-center gap-2 group'>
            <div
              className='size-8 bg-blue-600 rounded-lg flex items-center justify-center text-white 
              font-bold text-lg shadow-blue-200 shadow-lg group-hover:scale-105 transition-transform'
            >
              C
            </div>
            <span className='font-bold text-xl tracking-tight text-slate-800'>
              Cookbook
            </span>
          </NavLink>
        </div>

        <div className='flex justify-center'>
          <div
            className='hidden md:flex items-center bg-slate-100/80 p-1.5 rounded-full border 
            border-slate-200/50'
          >
            <NavLink to='/' className={isLinkActive}>
              Home
            </NavLink>
            <NavLink to='/recipes' className={isLinkActive}>
              Browse
            </NavLink>
            <NavLink to='/trending' className={isLinkActive}>
              Trending
            </NavLink>
            <NavLink to='/explore' className={isLinkActive}>
              Explore
            </NavLink>
          </div>
        </div>

        <div className='flex justify-end items-center gap-6'>
          <NavLink
            to='/create'
            className='hidden lg:flex items-center gap-2 text-sm font-medium text-slate-600 
              hover:text-blue-600 transition-colors'
          >
            <PlusIcon className='size-4 stroke-[2.5]' />
            <span>Create Recipe</span>
          </NavLink>

          <div className='h-6 w-px bg-slate-200 hidden lg:block'></div>

          <div className='flex items-center gap-3'>
            <NavLink
              to='/login'
              className='text-sm font-medium text-slate-600 hover:text-slate-900 px-3 py-2'
            >
              Log in
            </NavLink>
            <NavLink
              to='/signup'
              className='text-sm font-semibold bg-slate-900 text-white px-5 py-2.5 rounded-full
              hover:bg-slate-800 hover:shadow-lg hover:-translate-y-0.5 transition-all duration-200'
            >
              Sign up
            </NavLink>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
