/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
export type RecipeIngredientResponse = {
    id: number;
    name: string;
    type: RecipeIngredientResponse.type;
    amount: number;
    unit: string;
};
export namespace RecipeIngredientResponse {
    export enum type {
        VEGETABLE = 'VEGETABLE',
        FRUIT = 'FRUIT',
        MEAT = 'MEAT',
        DAIRY = 'DAIRY',
        GRAIN = 'GRAIN',
        SPICE = 'SPICE',
        MISC = 'MISC',
        SWEETENER = 'SWEETENER',
        SAUCE = 'SAUCE',
    }
}

