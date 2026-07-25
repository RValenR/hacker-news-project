export interface UsageLog {
  id?: number;
  timestamp: string;
  filterType: string;
  resultCount: number;
  responseTimeMs: number;
  endpoint?: string;
}