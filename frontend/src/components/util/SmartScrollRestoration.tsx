import { useEffect, useRef } from 'react';
import { useLocation } from 'react-router';

const SmartScrollRestoration = () => {
  const { pathname, search } = useLocation();
  const prevPathname = useRef(pathname);

  useEffect(() => {
    if (pathname !== prevPathname.current) {
      window.scrollTo(0, 0);
      prevPathname.current = pathname;
    }
  }, [pathname, search]);

  return null;
};

export default SmartScrollRestoration;
