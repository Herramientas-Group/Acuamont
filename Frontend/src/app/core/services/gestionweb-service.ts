import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { RedSocial } from '../../shared/interfaces/gestionweb';

@Injectable({
  providedIn: 'root',
})
export class GestionwebService {
  private logoApi = 'http://localhost:8080/logo/api';
  private redesApi = 'http://localhost:8080/redes/api';
  private slidesApi = 'http://localhost:8080/slides/api';

  constructor(private http: HttpClient) { }

  getLogoUrl(): Observable<string> {
    return this.http.get<{ success: boolean; data: string }>(`${this.logoApi}/url`, { withCredentials: true })
      .pipe(map(res => res.data));
  }

  subirLogo(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('logo', file);
    return this.http.post(`${this.logoApi}/guardar`, formData, { withCredentials: true });
  }

  listarRedesActivas(): Observable<RedSocial[]> {
    return this.http.get<RedSocial[]>(`${this.redesApi}/activas`);
  }

  listarRedes(): Observable<RedSocial[]> {
    return this.http.get<{ success: boolean; data: RedSocial[] }>(`${this.redesApi}/listar`, { withCredentials: true })
      .pipe(map(res => res.data));
  }

  actualizarRed(id: number, url: string): Observable<any> {
    return this.http.put(`${this.redesApi}/actualizar/${id}`, { url }, { withCredentials: true });
  }

  toggleEstadoRed(id: number): Observable<any> {
    return this.http.post(`${this.redesApi}/cambiar-estado/${id}`, {}, { withCredentials: true });
  }

  listarSlides(): Observable<string[]> {
    return this.http.get<string[]>(`${this.slidesApi}/listar-urls`, { withCredentials: true });
  }

  subirSlide(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('imagen', file);
    return this.http.post(`${this.slidesApi}/guardar`, formData, { withCredentials: true });
  }

  eliminarSlide(nombre: string): Observable<any> {
    return this.http.delete(`${this.slidesApi}/eliminar/${nombre}`, { withCredentials: true });
  }
}
