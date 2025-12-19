const PageLoader = () => {
  return (
    <div className='flex items-center justify-center min-h-screen bg-white'>
      <div className='flex flex-col items-center gap-4'>
        <div className='size-12 border-4 border-slate-200 border-t-blue-600 rounded-full animate-spin'></div>
        <span className='text-slate-500 font-medium animate-pulse'>
          Loading...
        </span>
      </div>
    </div>
  );
};

export default PageLoader;
