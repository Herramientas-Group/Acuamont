import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { ReporteUtilidadVenta, ReporteUtilidadUsuario, ReporteUtilidadProducto } from '../../shared/interfaces/utilidades';

@Injectable({
  providedIn: 'root',
})
export class UtilidadesService {
  private apiUrl = 'http://localhost:8080/reportes/api';

  constructor(private http: HttpClient) { }

  getUtilidadVentas(inicio?: string, fin?: string): Observable<ReporteUtilidadVenta[]> {
    if (inicio && fin) {
      let params = new HttpParams().set('inicio', inicio).set('fin', fin);
      return this.http.get<{ success: boolean; data: ReporteUtilidadVenta[] }>(`${this.apiUrl}/utilidad-ventas-rango`, { params })
        .pipe(map(res => res.data));
    }
    return this.http.get<{ success: boolean; data: ReporteUtilidadVenta[] }>(`${this.apiUrl}/utilidad-ventas`)
      .pipe(map(res => res.data));
  }

  getUtilidadUsuarios(inicio?: string, fin?: string): Observable<ReporteUtilidadUsuario[]> {
    if (inicio && fin) {
      let params = new HttpParams().set('inicio', inicio).set('fin', fin);
      return this.http.get<{ success: boolean; data: ReporteUtilidadUsuario[] }>(`${this.apiUrl}/utilidad-usuarios-rango`, { params })
        .pipe(map(res => res.data));
    }
    return this.http.get<{ success: boolean; data: ReporteUtilidadUsuario[] }>(`${this.apiUrl}/utilidad-usuarios`)
      .pipe(map(res => res.data));
  }

  getUtilidadProductos(inicio?: string, fin?: string): Observable<ReporteUtilidadProducto[]> {
    if (inicio && fin) {
      let params = new HttpParams().set('inicio', inicio).set('fin', fin);
      return this.http.get<{ success: boolean; data: ReporteUtilidadProducto[] }>(`${this.apiUrl}/utilidad-producto-rango`, { params })
        .pipe(map(res => res.data));
    }
    return this.http.get<{ success: boolean; data: ReporteUtilidadProducto[] }>(`${this.apiUrl}/utilidad-producto`)
      .pipe(map(res => res.data));
  }
}
