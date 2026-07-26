import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HackerNewsService } from './hacker-news.service';
import { Entry, FilterType } from '../models/entry.model';
import { UsageLog } from '../models/usage-log.model';
import { environment } from '../../../environments/environment';

describe('HackerNewsService', () => {
  let service: HackerNewsService;
  let httpMock: HttpTestingController;
  const apiUrl = environment.apiUrl;

  // Datos de prueba
  const mockEntries: Entry[] = [
    { id: 1, position: 1, title: 'Test Entry 1', points: 100, comments: 50, wordCount: 3 },
    { id: 2, position: 2, title: 'Test Entry 2 with more words', points: 200, comments: 30, wordCount: 6 }
  ];

  const mockUsageLogs: UsageLog[] = [
    { id: 1, timestamp: new Date().toISOString(), filterType: 'MORE_THAN_5', resultCount: 12, responseTimeMs: 245, endpoint: '/api/entries/filter/more-than-5' },
    { id: 2, timestamp: new Date().toISOString(), filterType: 'LESS_EQUAL_5', resultCount: 18, responseTimeMs: 189, endpoint: '/api/entries/filter/less-equal-5' }
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [HackerNewsService]
    });
    service = TestBed.inject(HackerNewsService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); // Verifica que no haya peticiones pendientes
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getEntries', () => {
    it('should fetch all entries', () => {
      service.getEntries().subscribe(entries => {
        expect(entries).toEqual(mockEntries);
        expect(entries.length).toBe(2);
      });

      const req = httpMock.expectOne(`${apiUrl}/entries`);
      expect(req.request.method).toBe('GET');
      req.flush(mockEntries);
    });

    it('should handle errors gracefully', () => {
      service.getEntries().subscribe({
        error: (error) => {
          expect(error.status).toBe(404);
        }
      });

      const req = httpMock.expectOne(`${apiUrl}/entries`);
      req.flush('Error loading entries', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('getFilteredEntries', () => {
    it('should fetch filtered entries for MORE_THAN_5', () => {
      const filterType: FilterType = 'MORE_THAN_5';
      
      service.getFilteredEntries(filterType).subscribe(entries => {
        expect(entries).toEqual(mockEntries);
        expect(entries.length).toBe(2);
      });

      const req = httpMock.expectOne(`${apiUrl}/entries/filter/more-than-5`);
      expect(req.request.method).toBe('GET');
      req.flush(mockEntries);
    });

    it('should fetch filtered entries for LESS_EQUAL_5', () => {
      const filterType: FilterType = 'LESS_EQUAL_5';
      
      service.getFilteredEntries(filterType).subscribe(entries => {
        expect(entries).toEqual(mockEntries);
      });

      const req = httpMock.expectOne(`${apiUrl}/entries/filter/less-equal-5`);
      expect(req.request.method).toBe('GET');
      req.flush(mockEntries);
    });

    it('should handle errors for filtered entries', () => {
      const filterType: FilterType = 'MORE_THAN_5';
      
      service.getFilteredEntries(filterType).subscribe({
        error: (error) => {
          expect(error.status).toBe(500);
        }
      });

      const req = httpMock.expectOne(`${apiUrl}/entries/filter/more-than-5`);
      req.flush('Server error', { status: 500, statusText: 'Internal Server Error' });
    });
  });

  describe('getUsageLogs', () => {
    it('should fetch usage logs', () => {
      service.getUsageLogs().subscribe(logs => {
        expect(logs).toEqual(mockUsageLogs);
        expect(logs.length).toBe(2);
      });

      const req = httpMock.expectOne(`${apiUrl}/entries/usage-logs`);
      expect(req.request.method).toBe('GET');
      req.flush(mockUsageLogs);
    });

    it('should handle empty logs', () => {
      service.getUsageLogs().subscribe(logs => {
        expect(logs).toEqual([]);
        expect(logs.length).toBe(0);
      });

      const req = httpMock.expectOne(`${apiUrl}/entries/usage-logs`);
      req.flush([]);
    });
  });

  describe('refreshEntries', () => {
    it('should refresh entries with POST request', () => {
      service.refreshEntries().subscribe(entries => {
        expect(entries).toEqual(mockEntries);
      });

      const req = httpMock.expectOne(`${apiUrl}/entries/refresh`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual({});
      req.flush(mockEntries);
    });

    it('should handle refresh errors', () => {
      service.refreshEntries().subscribe({
        error: (error) => {
          expect(error.status).toBe(503);
        }
      });

      const req = httpMock.expectOne(`${apiUrl}/entries/refresh`);
      req.flush('Service unavailable', { status: 503, statusText: 'Service Unavailable' });
    });
  });
});