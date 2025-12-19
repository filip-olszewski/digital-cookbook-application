import type { CategorySearchCriteria } from '../types/categoryTypes';
import { CategoryControllerService } from './generated';

export const fetchCategories = async (criteria: CategorySearchCriteria) => {
  return await CategoryControllerService.getAllCategories({ ...criteria });
};
