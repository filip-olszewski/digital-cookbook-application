import { NavLink } from 'react-router';
import { ArrowRightIcon, PlayCircleIcon } from '@heroicons/react/24/outline';

const HeroSection = () => {
  return (
    <section className='relative w-full bg-slate-50 overflow-hidden'>
      <div className='absolute top-0 right-0 -translate-y-1/2 translate-x-1/3 w-[800px] h-[800px] bg-blue-100/50 rounded-full blur-3xl opacity-50 pointer-events-none' />
      <div className='absolute bottom-0 left-0 translate-y-1/3 -translate-x-1/4 w-[600px] h-[600px] bg-indigo-100/60 rounded-full blur-3xl opacity-50 pointer-events-none' />

      <div className='max-w-7xl mx-auto px-6 py-20 lg:py-28 grid lg:grid-cols-2 gap-12 items-center relative z-10 mt-10'>
        <div className='max-w-2xl'>
          <div
            className='inline-flex items-center gap-2 px-3 py-1 rounded-full bg-indigo-100
           text-indigo-700 text-xs font-bold uppercase tracking-wider mb-6 cursor-default'
          >
            <span className='w-2 h-2 rounded-full bg-indigo-500 animate-pulse' />
            New Recipes Added Daily
          </div>

          <h1 className='text-5xl lg:text-7xl font-bold text-slate-900 leading-[1.1] mb-6 tracking-tight'>
            Cook Like a <br />
            <span className='text-transparent bg-clip-text bg-gradient-to-r from-blue-500 to-blue-600'>
              Master Chef
            </span>
          </h1>

          <p className='text-lg text-slate-600 mb-8 leading-relaxed max-w-lg'>
            Join thousands of food lovers. Discover tested recipes, organize
            your meals, and bring the joy of cooking back to your kitchen.
          </p>

          <div className='flex flex-wrap items-center gap-4'>
            <NavLink
              to='/recipes'
              className='px-8 py-4 rounded-xl bg-slate-900 text-white font-semibold hover:bg-slate-800 hover:-translate-y-1 transition-all shadow-xl shadow-slate-900/20 flex items-center gap-2'
            >
              Start Cooking
              <ArrowRightIcon className='size-5' />
            </NavLink>
            <NavLink
              to='/about'
              className='px-8 py-4 rounded-xl bg-white border border-slate-200 text-slate-700 font-semibold hover:bg-slate-50 hover:border-slate-300 transition-all flex items-center gap-2'
            >
              <PlayCircleIcon className='size-6 text-slate-400' />
              How it works
            </NavLink>
          </div>

          <div className='mt-12 flex items-center gap-8 border-t border-slate-200 pt-8'>
            <div>
              <p className='text-3xl font-bold text-slate-900'>1.2k+</p>
              <p className='text-sm text-slate-500'>Recipes</p>
            </div>
            <div className='w-px h-10 bg-slate-200' />
            <div>
              <p className='text-3xl font-bold text-slate-900'>500+</p>
              <p className='text-sm text-slate-500'>Community Chefs</p>
            </div>
          </div>
        </div>

        <div className='relative hidden lg:block'>
          <div className='relative z-10 rounded-3xl overflow-hidden shadow-2xl shadow-slate-900/20 rotate-2 hover:rotate-0 transition-transform duration-500'>
            <img
              src='https://images.unsplash.com/photo-1659354219064-1edc322b524b?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D'
              alt='Chef Cooking'
              className='w-full h-auto object-cover'
            />
          </div>
          <div className='absolute -bottom-6 -right-6 w-full h-full border-2 border-slate-200 rounded-3xl -z-10' />
        </div>
      </div>
    </section>
  );
};

export default HeroSection;
