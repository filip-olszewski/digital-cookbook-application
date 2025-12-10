/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { UserCreateRequest } from '../models/UserCreateRequest';
import type { UserSummaryResponse } from '../models/UserSummaryResponse';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class UserControllerService {
    /**
     * @returns UserSummaryResponse OK
     * @throws ApiError
     */
    public static registerUser({
        requestBody,
    }: {
        requestBody: UserCreateRequest,
    }): CancelablePromise<UserSummaryResponse> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/v1/users',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
}
