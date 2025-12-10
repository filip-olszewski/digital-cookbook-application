/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { IngredientCreateRequest } from '../models/IngredientCreateRequest';
import type { IngredientSummaryResponse } from '../models/IngredientSummaryResponse';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class IngredientControllerService {
    /**
     * @returns IngredientSummaryResponse OK
     * @throws ApiError
     */
    public static addIngredient({
        requestBody,
    }: {
        requestBody: IngredientCreateRequest,
    }): CancelablePromise<IngredientSummaryResponse> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/v1/ingredients',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
}
