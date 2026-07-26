import { Component, OnInit } from '@angular/core';
import { HackerNewsService } from './core/services/hacker-news.service';
import { Entry, FilterType } from './core/models/entry.model';
import { UsageLog } from './core/models/usage-log.model';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  entries: Entry[] = [];
  filteredEntries: Entry[] = [];
  usageLogs: UsageLog[] = [];
  currentFilter: FilterType = 'NONE';
  isLoading = false;
  errorMessage: string | null = null;
  showModal = false; // ← Controla el modal

  totalEntries = 0;
  avgPoints = 0;
  totalComments = 0;

  constructor(private hackerNewsService: HackerNewsService) {}

  ngOnInit(): void {
    this.loadEntries();
    this.loadUsageLogs();
  }

  loadEntries(): void {
    this.isLoading = true;
    this.errorMessage = null;
    
    this.hackerNewsService.getEntries().subscribe({
      next: (data) => {
        this.entries = data;
        this.filteredEntries = data;
        this.currentFilter = 'NONE';
        this.calculateStats();
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = 'Error loading entries. Please try again.';
        this.isLoading = false;
        console.error('Error:', error);
      }
    });
  }

  applyFilter(filterType: FilterType): void {
    if (filterType === 'NONE') {
      this.filteredEntries = this.entries;
      this.currentFilter = 'NONE';
      this.calculateStats();
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;

    this.hackerNewsService.getFilteredEntries(filterType).subscribe({
      next: (data) => {
        this.filteredEntries = data;
        this.currentFilter = filterType;
        this.calculateStats();
        this.isLoading = false;
        this.loadUsageLogs(); // ← Actualizar logs al aplicar filtro
      },
      error: (error) => {
        this.errorMessage = 'Error applying filter. Please try again.';
        this.isLoading = false;
        console.error('Error:', error);
      }
    });
  }

  loadUsageLogs(): void {
    this.hackerNewsService.getUsageLogs().subscribe({
      next: (data) => {
        this.usageLogs = data.slice(0, 20); // ← Más logs para el modal
        console.log('📊 Logs cargados:', this.usageLogs.length);
      },
      error: (error) => {
        console.error('Error loading logs:', error);
        // Si hay error, mostrar logs de prueba (para desarrollo)
        this.usageLogs = [
          {
            timestamp: new Date().toISOString(),
            filterType: 'MORE_THAN_5',
            resultCount: 12,
            responseTimeMs: 245,
            endpoint: '/api/entries/filter/more-than-5'
          },
          {
            timestamp: new Date(Date.now() - 60000).toISOString(),
            filterType: 'LESS_EQUAL_5',
            resultCount: 18,
            responseTimeMs: 189,
            endpoint: '/api/entries/filter/less-equal-5'
          }
        ];
      }
    });
  }

  calculateStats(): void {
    this.totalEntries = this.filteredEntries.length;
    this.avgPoints = this.filteredEntries.length > 0 
      ? Math.round(this.filteredEntries.reduce((sum, e) => sum + e.points, 0) / this.filteredEntries.length)
      : 0;
    this.totalComments = this.filteredEntries.reduce((sum, e) => sum + e.comments, 0);
  }

  refreshData(): void {
    this.loadEntries();
    this.loadUsageLogs();
  }

  getFilterLabel(filter: FilterType): string {
    switch(filter) {
      case 'MORE_THAN_5': return 'More than 5 words';
      case 'LESS_EQUAL_5': return '≤ 5 words';
      default: return 'All entries';
    }
  }

  // 🆕 MÉTODOS PARA EL MODAL
  openModal(): void {
    this.showModal = true;
    document.body.style.overflow = 'hidden';
    this.loadUsageLogs(); // ← Recargar logs al abrir el modal
  }

  closeModal(): void {
    this.showModal = false;
    document.body.style.overflow = 'auto';
  }
}