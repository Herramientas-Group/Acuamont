import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DashboardService } from '../../../core/services/dashboard-service';
import { DashboardResumen } from '../../../shared/interfaces/dashboard';

@Component({
  selector: 'app-dashboard-component',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard-component.html'
})
export class DashboardComponent implements OnInit {
  resumen: DashboardResumen | null = null;

  constructor(
    private dashboardService: DashboardService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.dashboardService.getResumen().subscribe({
      next: (data) => {
        this.resumen = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar dashboard:', err);
        this.cdr.detectChanges();
      }
    });
  }
}
