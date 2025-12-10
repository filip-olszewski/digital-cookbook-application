/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { CategoryDetailsResponse } from '../models/CategoryDetailsResponse';
import type { CategorySearchCriteria } from '../models/CategorySearchCriteria';
import type { CategorySummaryResponse } from '../models/CategorySummaryResponse';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class CategoryControllerService {
    /**
     * @returns CategorySummaryResponse OK
     * @throws ApiError
     */
    public static getAllCategories({
        criteria,
    }: {
        criteria: CategorySearchCriteria,
    }): CancelablePromise<Array<CategorySummaryResponse>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/categories',
            query: {
                'criteria': criteria,
            },
        });
    }
    /**
     * @returns CategoryDetailsResponse OK
     * @throws ApiError
     */
    public static getCategory({
        slug,
    }: {
        slug: string,
    }): CancelablePromise<CategoryDetailsResponse> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/categories/{slug}',
            path: {
                'slug': slug,
            },
        });
    }
}
