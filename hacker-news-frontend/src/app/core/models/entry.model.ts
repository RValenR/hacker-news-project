export interface Entry {
  id?: number;
  position: number;
  title: string;
  points: number;
  comments: number;
  wordCount: number;
}

export type FilterType = 'NONE' | 'MORE_THAN_5' | 'LESS_EQUAL_5';