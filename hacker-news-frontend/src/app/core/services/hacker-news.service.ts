import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Entry, FilterType } from '../models/entry.model';
import { UsageLog } from '../models/usage-log.model';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class HackerNewsService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getEntries(): Observable<Entry[]> {
    return this.http.get<Entry[]>(`${this.apiUrl}/entries`);
  }

  getFilteredEntries(filterType: FilterType): Observable<Entry[]> {
    const endpoint = filterType === 'MORE_THAN_5' 
      ? '/entries/filter/more-than-5' 
      : '/entries/filter/less-equal-5';
    return this.http.get<Entry[]>(`${this.apiUrl}${endpoint}`);
  }

  getUsageLogs(): Observable<UsageLog[]> {
    return this.http.get<UsageLog[]>(`${this.apiUrl}/usage-logs`);
  }

  refreshEntries(): Observable<Entry[]> {
    return this.http.post<Entry[]>(`${this.apiUrl}/entries/refresh`, {});
  }
}