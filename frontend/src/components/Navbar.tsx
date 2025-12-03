import { NavLink } from 'react-router';

const Navbar = () => {
  const test = () => {
    alert(1);
  };

  const isLinkActive = ({ isActive }: { isActive: boolean }) => {
    return `font-medium transition-colors duartion-300 hover:text-blue-400 ${
      isActive ? 'text-blue-600 font-semibold' : 'text-gray-500'
    }`;
  };

  return (
    <nav className='flex justify-between items-center px-48 py-6 font-grotesk fixed w-full z-100'>
      <div className='flex flex-1 justify-start'>
        <NavLink to='/' className='font-semibold'>
          Digital Cookbook
        </NavLink>
      </div>

      <div className='flex flex-1 justify-center'>
        <div className='flex bg-slate-100 py-4 px-6 rounded-xl gap-8'>
          <NavLink to='/' className={isLinkActive}>
            Home
          </NavLink>
          <NavLink to='/recipes' className={isLinkActive}>
            Browse Recipes
          </NavLink>
          <NavLink to='/trending' className={isLinkActive}>
            Trending
          </NavLink>
          <NavLink to='/explore' className={isLinkActive}>
            Explore
          </NavLink>
        </div>
      </div>

      <div className='flex flex-1 justify-end gap-8'>
        <NavLink to='/' className='font-semibold'>
          Create recipe
        </NavLink>
        <NavLink to='/' className='font-semibold'>
          Sign in
        </NavLink>
        <NavLink to='/' className='font-semibold'>
          Sign up
        </NavLink>
      </div>
    </nav>
  );
};

export default Navbar;
