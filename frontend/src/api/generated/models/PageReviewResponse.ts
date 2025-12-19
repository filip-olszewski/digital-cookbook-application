/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { PageableObject } from './PageableObject';
import type { ReviewResponse } from './ReviewResponse';
import type { SortObject } from './SortObject';
export type PageReviewResponse = {
    totalElements?: number;
    totalPages?: number;
    first?: boolean;
    last?: boolean;
    size?: number;
    content?: Array<ReviewResponse>;
    number?: number;
    sort?: SortObject;
    numberOfElements?: number;
    pageable?: PageableObject;
    empty?: boolean;
};

