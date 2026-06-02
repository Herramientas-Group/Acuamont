import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Proveedor } from '../../shared/interfaces/proveedor';

@Injectable({
  providedIn: 'root',
})
export class ProveedoresService {
  private apiUrl = 'http://localhost:8080/proveedores/api';

  constructor(private http: HttpClient) { }

  listarProveedores(): Observable<Proveedor[]> {
    return this.http.get<{ success: boolean, data: Proveedor[] }>(`${this.apiUrl}/listar`)
      .pipe(map(res => res.data));
  }

  guardarProveedor(proveedor: Proveedor): Observable<any> {
    return this.http.post(`${this.apiUrl}/guardar`, proveedor);
  }

  obtenerProveedor(id: number): Observable<{ success: boolean, data: Proveedor }> {
    return this.http.get<{ success: boolean, data: Proveedor }>(`${this.apiUrl}/${id}`);
  }

  eliminarProveedor(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/eliminar/${id}`);
  }

  cambiarEstado(id: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/cambiar-estado/${id}`, {});
  }

  buscarPorDocumento(documento: string): Observable<{ success: boolean, data: Proveedor }> {
    return this.http.get<{ success: boolean, data: Proveedor }>(`${this.apiUrl}/buscar-proveedor-documento/${documento}`);
  }
}
