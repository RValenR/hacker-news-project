import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';
import { AppComponent } from './app.component';
import { HackerNewsService } from './core/services/hacker-news.service';
import { Entry } from './core/models/entry.model';
import { UsageLog } from './core/models/usage-log.model';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let mockService: jasmine.SpyObj<HackerNewsService>;

  const mockEntries: Entry[] = [
    { id: 1, position: 1, title: 'Test Entry 1', points: 100, comments: 50, wordCount: 3 },
    { id: 2, position: 2, title: 'Test Entry 2 with more words', points: 200, comments: 30, wordCount: 6 }
  ];

  const mockFilteredEntries: Entry[] = [
    { id: 2, position: 2, title: 'Test Entry 2 with more words', points: 200, comments: 30, wordCount: 6 }
  ];

  const mockUsageLogs: UsageLog[] = [
    { id: 1, timestamp: new Date().toISOString(), filterType: 'MORE_THAN_5', resultCount: 12, responseTimeMs: 245, endpoint: '/api/entries/filter/more-than-5' }
  ];

  beforeEach(async () => {
    mockService = jasmine.createSpyObj('HackerNewsService', [
      'getEntries',
      'getFilteredEntries',
      'getUsageLogs',
      'refreshEntries'
    ]);

    await TestBed.configureTestingModule({
      declarations: [AppComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: HackerNewsService, useValue: mockService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
  });

  describe('Carga inicial', () => {
    it('should create the app', () => {
      expect(component).toBeTruthy();
    });

    it('should load entries on init', () => {
      mockService.getEntries.and.returnValue(of(mockEntries));
      mockService.getUsageLogs.and.returnValue(of(mockUsageLogs));

      fixture.detectChanges();

      expect(mockService.getEntries).toHaveBeenCalled();
      expect(component.entries).toEqual(mockEntries);
      expect(component.filteredEntries).toEqual(mockEntries);
      expect(component.totalEntries).toBe(2);
      expect(component.isLoading).toBeFalse();
    });

    it('should handle error when loading entries', () => {
      mockService.getEntries.and.returnValue(throwError(() => new Error('Error loading entries')));
      mockService.getUsageLogs.and.returnValue(of([]));

      fixture.detectChanges();

      expect(component.errorMessage).toContain('Error loading entries');
      expect(component.isLoading).toBeFalse();
    });
  });

  describe('Filtros', () => {
    beforeEach(() => {
      mockService.getEntries.and.returnValue(of(mockEntries));
      mockService.getUsageLogs.and.returnValue(of(mockUsageLogs));
      fixture.detectChanges();
    });

    it('should apply filter NONE', () => {
      component.applyFilter('NONE');
      
      expect(component.filteredEntries).toEqual(mockEntries);
      expect(component.currentFilter).toBe('NONE');
      expect(component.totalEntries).toBe(2);
    });

    it('should apply filter MORE_THAN_5', () => {
      mockService.getFilteredEntries.and.returnValue(of(mockFilteredEntries));
      mockService.getUsageLogs.and.returnValue(of(mockUsageLogs));

      component.applyFilter('MORE_THAN_5');

      expect(mockService.getFilteredEntries).toHaveBeenCalledWith('MORE_THAN_5');
      expect(component.filteredEntries).toEqual(mockFilteredEntries);
      expect(component.currentFilter).toBe('MORE_THAN_5');
      expect(component.totalEntries).toBe(1);
      expect(component.isLoading).toBeFalse();
    });

    it('should apply filter LESS_EQUAL_5', () => {
      const lessEqualEntries = [mockEntries[0]];
      mockService.getFilteredEntries.and.returnValue(of(lessEqualEntries));
      mockService.getUsageLogs.and.returnValue(of(mockUsageLogs));

      component.applyFilter('LESS_EQUAL_5');

      expect(mockService.getFilteredEntries).toHaveBeenCalledWith('LESS_EQUAL_5');
      expect(component.filteredEntries).toEqual(lessEqualEntries);
      expect(component.currentFilter).toBe('LESS_EQUAL_5');
      expect(component.totalEntries).toBe(1);
    });

    it('should handle error when applying filter', () => {
      mockService.getFilteredEntries.and.returnValue(throwError(() => new Error('Error applying filter')));

      component.applyFilter('MORE_THAN_5');

      expect(component.errorMessage).toContain('Error applying filter');
      expect(component.isLoading).toBeFalse();
    });
  });

  describe('Estadísticas', () => {
    it('should calculate stats correctly', () => {
      component.filteredEntries = mockEntries;
      component.calculateStats();

      expect(component.totalEntries).toBe(2);
      expect(component.avgPoints).toBe(150);
      expect(component.totalComments).toBe(80);
    });

    it('should handle empty entries for stats', () => {
      component.filteredEntries = [];
      component.calculateStats();

      expect(component.totalEntries).toBe(0);
      expect(component.avgPoints).toBe(0);
      expect(component.totalComments).toBe(0);
    });
  });

  describe('Logs de uso', () => {
    it('should load usage logs', () => {
      mockService.getUsageLogs.and.returnValue(of(mockUsageLogs));

      component.loadUsageLogs();

      expect(mockService.getUsageLogs).toHaveBeenCalled();
      expect(component.usageLogs).toEqual(mockUsageLogs);
    });

    it('should handle error when loading logs', () => {
      const consoleErrorSpy = spyOn(console, 'error');
      mockService.getUsageLogs.and.returnValue(throwError(() => new Error('Error loading logs')));

      component.loadUsageLogs();

      expect(consoleErrorSpy).toHaveBeenCalled();
    });
  });

  describe('Modal', () => {
    // ✅ CORREGIDO: Configurar el mock correctamente antes de abrir el modal
    it('should open modal and load logs', () => {
      // Configurar el mock ANTES de abrir el modal
      mockService.getUsageLogs.and.returnValue(of(mockUsageLogs));
      
      const originalOverflow = document.body.style.overflow;
      
      component.openModal();
      
      expect(component.showModal).toBeTrue();
      expect(document.body.style.overflow).toBe('hidden');
      expect(mockService.getUsageLogs).toHaveBeenCalled();
      expect(component.usageLogs).toEqual(mockUsageLogs);
      
      document.body.style.overflow = originalOverflow;
    });

    it('should close modal and restore body scroll', () => {
      component.showModal = true;
      document.body.style.overflow = 'hidden';
      
      component.closeModal();
      
      expect(component.showModal).toBeFalse();
      expect(document.body.style.overflow).toBe('auto');
    });
  });

  describe('Refresh', () => {
    it('should refresh data', () => {
      mockService.getEntries.and.returnValue(of(mockEntries));
      mockService.getUsageLogs.and.returnValue(of(mockUsageLogs));

      component.refreshData();

      expect(mockService.getEntries).toHaveBeenCalled();
      expect(mockService.getUsageLogs).toHaveBeenCalled();
    });
  });

  describe('getFilterLabel', () => {
    it('should return correct labels for filters', () => {
      expect(component.getFilterLabel('NONE')).toBe('All entries');
      expect(component.getFilterLabel('MORE_THAN_5')).toBe('More than 5 words');
      expect(component.getFilterLabel('LESS_EQUAL_5')).toBe('≤ 5 words');
    });
  });
});