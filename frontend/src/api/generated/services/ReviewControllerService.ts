/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { PageReviewResponse } from '../models/PageReviewResponse';
import type { ReviewPostRequest } from '../models/ReviewPostRequest';
import type { ReviewResponse } from '../models/ReviewResponse';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class ReviewControllerService {
    /**
     * @returns PageReviewResponse OK
     * @throws ApiError
     */
    public static getAllReviews({
        slug,
        page,
        size = 24,
        sort,
    }: {
        slug: string,
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
    }): CancelablePromise<PageReviewResponse> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/recipes/{slug}/reviews',
            path: {
                'slug': slug,
            },
            query: {
                'page': page,
                'size': size,
                'sort': sort,
            },
        });
    }
    /**
     * @returns ReviewResponse OK
     * @throws ApiError
     */
    public static postReview({
        slug,
        requestBody,
    }: {
        slug: string,
        requestBody: ReviewPostRequest,
    }): CancelablePromise<ReviewResponse> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/v1/recipes/{slug}/reviews',
            path: {
                'slug': slug,
            },
            body: requestBody,
            mediaType: 'application/json',
        });
    }
}
