/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
export type IngredientCreateRequest = {
    name: string;
    type: IngredientCreateRequest.type;
};
export namespace IngredientCreateRequest {
    export enum type {
        VEGETABLE = 'VEGETABLE',
        FRUIT = 'FRUIT',
        MEAT = 'MEAT',
        DAIRY = 'DAIRY',
        GRAIN = 'GRAIN',
        SPICE = 'SPICE',
        MISC = 'MISC',
    }
}

