/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { PageRecipeSummaryResponse } from '../models/PageRecipeSummaryResponse';
import type { RecipeDetailsResponse } from '../models/RecipeDetailsResponse';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class RecipeControllerService {
    /**
     * @returns PageRecipeSummaryResponse OK
     * @throws ApiError
     */
    public static getRecipes({
        maxPrepTime,
        minRating,
        tags,
        categories,
        page,
        size = 24,
        sort,
    }: {
        maxPrepTime?: number,
        minRating?: number,
        tags?: Array<number>,
        categories?: Array<number>,
        /**
         * Zero-based page index (0..N)
         */
        page?: number,
        /**
         * The size of the page to be returned
         */
        size?: number,
        /**
         * Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
         */
        sort?: Array<string>,
    }): CancelablePromise<PageRecipeSummaryResponse> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/recipes',
            query: {
                'maxPrepTime': maxPrepTime,
                'minRating': minRating,
                'tags': tags,
                'categories': categories,
                'page': page,
                'size': size,
                'sort': sort,
            },
        });
    }
    /**
     * @returns RecipeDetailsResponse OK
     * @throws ApiError
     */
    public static getRecipe({
        slug,
    }: {
        slug: string,
    }): CancelablePromise<RecipeDetailsResponse> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/recipes/{slug}',
            path: {
                'slug': slug,
            },
        });
    }
}
