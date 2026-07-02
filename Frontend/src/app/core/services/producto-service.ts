import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Producto } from '../../shared/interfaces/producto';
import { Categoria } from '../../shared/interfaces/categoria';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ProductoService {
  private apiUrl = `${environment.apiUrl}/productos/api`;

  constructor(private http: HttpClient) { }

  listarProductos(): Observable<Producto[]> {
    return this.http.get<{ success: boolean, data: Producto[] }>(`${this.apiUrl}/listar`)
      .pipe(map(res => res.data));
  }

  listarCategoriasActivas(): Observable<Categoria[]> {
    return this.http.get<{ success: boolean, data: Categoria[] }>(`${this.apiUrl}/categorias`)
      .pipe(map(res => res.data));
  }

  guardarProducto(formData: FormData): Observable<any> {
    return this.http.post(`${this.apiUrl}/guardar`, formData);
  }

  obtenerProducto(id: number): Observable<{ success: boolean, data: Producto }> {
    return this.http.get<{ success: boolean, data: Producto }>(`${this.apiUrl}/${id}`);
  }

  eliminarProducto(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/eliminar/${id}`);
  }

  cambiarEstado(id: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/cambiar-estado/${id}`, {});
  }

  eliminarImagen(productoId: number, nombreImagen: string): Observable<any> {
    const formData = new FormData();
    formData.append('productoId', productoId.toString());
    formData.append('nombreImagen', nombreImagen);
    return this.http.post(`${this.apiUrl}/eliminar-imagen`, formData);
  }
}
