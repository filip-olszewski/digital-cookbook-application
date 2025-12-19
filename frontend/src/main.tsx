import { lazy, StrictMode, Suspense } from 'react';
import { createRoot } from 'react-dom/client';
import './index.css';
import {
  createBrowserRouter,
  createRoutesFromElements,
  Route,
  RouterProvider,
} from 'react-router';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import MainLayout from './layouts/MainLayout.tsx';
import HomePage from './pages/HomePage.tsx';
import PageLoader from './components/ui/PageLoader.tsx';

const BrowseRecipesPage = lazy(() => import('./pages/BrowseRecipesPage.tsx'));
const RecipePage = lazy(() => import('./pages/RecipePage.tsx'));

const router = createBrowserRouter(
  createRoutesFromElements(
    <Route
      element={
        <Suspense fallback={<PageLoader />}>
          <MainLayout />
        </Suspense>
      }
    >
      <Route index element={<HomePage />} />
      <Route path='/recipes' element={<BrowseRecipesPage />} />
      <Route path='/recipes/:slug' element={<RecipePage />} />
    </Route>
  )
);

const queryClient = new QueryClient();

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <RouterProvider router={router} />
    </QueryClientProvider>
  </StrictMode>
);
