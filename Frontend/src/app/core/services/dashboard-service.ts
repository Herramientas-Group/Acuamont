import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { DashboardResumen } from '../../shared/interfaces/dashboard';

@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  private apiUrl = 'http://localhost:8080/dashboard/api';

  constructor(private http: HttpClient) { }

  getResumen(): Observable<DashboardResumen> {
    return this.http.get<{ success: boolean; data: DashboardResumen }>(`${this.apiUrl}/resumen`)
      .pipe(map(res => res.data));
  }
}
