export type Pageable = {
  page: number;
  size: number;
  sort: PageableSort;
};

export type PageableSort = {
  sortBy: string;
  order: 'asc' | 'desc';
};
