/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { UserSummaryResponse } from './UserSummaryResponse';
export type ReviewResponse = {
    id: number;
    rating: number;
    comment?: string;
    postedAt: string;
    user: UserSummaryResponse;
};

