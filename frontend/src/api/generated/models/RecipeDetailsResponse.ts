/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { CategorySummaryResponse } from './CategorySummaryResponse';
import type { RecipeIngredientResponse } from './RecipeIngredientResponse';
import type { StepResponse } from './StepResponse';
import type { TagResponse } from './TagResponse';
import type { UserSummaryResponse } from './UserSummaryResponse';
export type RecipeDetailsResponse = {
    id: number;
    name: string;
    slug: string;
    description: string;
    prepTime: number;
    servings: number;
    publicationDate: string;
    imgUrl?: string;
    rating: number;
    reviewCount: number;
    favouriteCount: number;
    category: CategorySummaryResponse;
    author: UserSummaryResponse;
    tags: Array<TagResponse>;
    ingredients: Array<RecipeIngredientResponse>;
    steps: Array<StepResponse>;
};

