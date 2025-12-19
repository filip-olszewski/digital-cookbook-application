export type RecipeSearchCriteria = {
  name?: string;
  maxPrepTime?: number;
  minRating?: number;
  tags?: Array<string>;
  categories?: Array<string>;
};
