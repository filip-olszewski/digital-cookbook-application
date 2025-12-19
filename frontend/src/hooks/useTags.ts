import { keepPreviousData, useQuery } from '@tanstack/react-query';
import { fetchTags } from '../api/tagApi';

export const useTags = () => {
  const { data, error, isPending } = useQuery({
    queryKey: ['tags'],
    queryFn: () => fetchTags(),
    placeholderData: keepPreviousData,
    staleTime: 60 * 60 * 1000,
  });

  return { data, error, isPending };
};
