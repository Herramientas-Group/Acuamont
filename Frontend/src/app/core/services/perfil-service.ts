import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Perfil, Opcion } from '../../shared/interfaces/perfil';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class PerfilService {
  private apiUrl = `${environment.apiUrl}/perfiles`;

  constructor(private http: HttpClient) { }

  listarPerfiles(): Observable<Perfil[]> {
    return this.http.get<{ success: boolean, data: Perfil[] }>(`${this.apiUrl}/api/listar`)
      .pipe(map(res => res.data));
  }

  obtenerPerfil(id: number): Observable<any> {
    return this.http.get<{ success: boolean, data: any }>(`${this.apiUrl}/api/${id}`)
      .pipe(map(res => res.data));
  }

  guardarPerfil(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/api/guardar`, data);
  }

  cambiarEstado(id: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/api/cambiar-estado/${id}`, {});
  }

  eliminarPerfil(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/api/eliminar/${id}`);
  }

  listarOpciones(): Observable<Opcion[]> {
    return this.http.get<{ success: boolean, data: Opcion[] }>(`${this.apiUrl}/api/opciones`)
      .pipe(map(res => res.data));
  }
}
