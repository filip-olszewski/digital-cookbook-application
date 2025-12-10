import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { fetchRecipe } from "../api/recipeApi";

export const useRecipe = (slug: string | undefined) => {
   const { data, error, isPending } = useQuery({
    queryKey: ['recipes', slug],
    queryFn: () => fetchRecipe(slug!),
    placeholderData: keepPreviousData,
    enabled: !!slug
  });

  return { data, error, isPending };
} 