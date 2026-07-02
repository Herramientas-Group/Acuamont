import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Categoria } from '../../shared/interfaces/categoria';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class CategoriaService {
  private apiUrl = `${environment.apiUrl}/categorias/api`;

  constructor(private http: HttpClient) { }

  listarCategorias(): Observable<Categoria[]> {
    return this.http.get<{ success: boolean, data: Categoria[] }>(`${this.apiUrl}/listar`)
      .pipe(map(res => res.data));
  }

  guardarCategoria(categoria: Categoria): Observable<any> {
    return this.http.post(`${this.apiUrl}/guardar`, categoria);
  }

  obtenerCategoria(id: number): Observable<{ success: boolean, data: Categoria }> {
    return this.http.get<{ success: boolean, data: Categoria }>(`${this.apiUrl}/${id}`);
  }

  eliminarCategoria(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/eliminar/${id}`);
  }

  cambiarEstado(id: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/cambiar-estado/${id}`, {});
  }
}
