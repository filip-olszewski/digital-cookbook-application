/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
export type IngredientSummaryResponse = {
    id?: number;
    name?: string;
    type?: IngredientSummaryResponse.type;
};
export namespace IngredientSummaryResponse {
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

