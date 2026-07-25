import { Component, OnInit } from '@angular/core';
import { Entry, FilterType } from './core/models/entry.model';
import { UsageLog } from './core/models/usage-log.model';
import { HackerNewsService } from './core/services/hacker-news.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  entries: Entry[] = [];
  filteredEntries: Entry[] = [];
  usageLogs: UsageLog[] = [];
  currentFilter: FilterType = 'NONE';
  isLoading = false;
  errorMessage: string | null = null;

  // Estadísticas
  totalEntries = 0;
  avgPoints = 0;
  totalComments = 0;

  constructor(private hackerNewsService: HackerNewsService) {}

  ngOnInit(): void {
    this.loadEntries();
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
        this.loadUsageLogs();
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
        this.usageLogs = data.slice(0, 10);
      },
      error: (error) => console.error('Error loading logs:', error)
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
}