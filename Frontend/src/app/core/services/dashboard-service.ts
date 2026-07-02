import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { DashboardResumen } from '../../shared/interfaces/dashboard';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  private apiUrl = `${environment.apiUrl}/dashboard/api`;

  constructor(private http: HttpClient) { }

  getResumen(): Observable<DashboardResumen> {
    return this.http.get<{ success: boolean; data: DashboardResumen }>(`${this.apiUrl}/resumen`)
      .pipe(map(res => res.data));
  }
}
