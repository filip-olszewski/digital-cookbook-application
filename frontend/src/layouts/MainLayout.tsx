import Footer from '../components/layout/Footer';
import Navbar from '../components/layout/Navbar';
import { Outlet } from 'react-router';
import SmartScrollRestoration from '../components/util/SmartScrollRestoration';

const MainLayout = () => {
  return (
    <>
      <SmartScrollRestoration />

      <Navbar />
      <main>
        <Outlet />
      </main>
      <Footer />
    </>
  );
};

export default MainLayout;
