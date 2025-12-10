/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { PageableObject } from './PageableObject';
import type { RecipeSummaryResponse } from './RecipeSummaryResponse';
import type { SortObject } from './SortObject';
export type PageRecipeSummaryResponse = {
    totalElements?: number;
    totalPages?: number;
    size?: number;
    content?: Array<RecipeSummaryResponse>;
    number?: number;
    first?: boolean;
    last?: boolean;
    numberOfElements?: number;
    sort?: SortObject;
    pageable?: PageableObject;
    empty?: boolean;
};

