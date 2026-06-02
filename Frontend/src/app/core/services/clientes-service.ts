import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Cliente } from '../../shared/interfaces/cliente';

@Injectable({
  providedIn: 'root',
})
export class ClientesService {
  private apiUrl = 'http://localhost:8080/clientes/api';

  constructor(private http: HttpClient) { }

  listarClientes(): Observable<Cliente[]> {
    return this.http.get<{ success: boolean, data: Cliente[] }>(`${this.apiUrl}/listar`)
      .pipe(map(res => res.data));
  }

  guardarCliente(cliente: Cliente): Observable<any> {
    return this.http.post(`${this.apiUrl}/guardar`, cliente);
  }

  obtenerCliente(id: number): Observable<{ success: boolean, data: Cliente }> {
    return this.http.get<{ success: boolean, data: Cliente }>(`${this.apiUrl}/${id}`);
  }

  eliminarCliente(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/eliminar/${id}`);
  }

  cambiarEstado(id: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/cambiar-estado/${id}`, {});
  }

  buscarPorDocumento(documento: string): Observable<{ success: boolean, data: Cliente }> {
    return this.http.get<{ success: boolean, data: Cliente }>(`${this.apiUrl}/buscar-cliente-documento/${documento}`);
  }
}
