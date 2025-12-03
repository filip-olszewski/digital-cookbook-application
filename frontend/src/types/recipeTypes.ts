export type RecipeSummary = {
  id: number;
  name: string;
  slug: string;
  prepTime: number;
  imgUrl: string;
  rating: number;
  authorName: string;
  category: string;
  tags: string[];
};
