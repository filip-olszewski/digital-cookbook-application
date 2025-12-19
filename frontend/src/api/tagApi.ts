import { TagControllerService } from './generated';

export const fetchTags = async () => {
  return await TagControllerService.getTags();
};
