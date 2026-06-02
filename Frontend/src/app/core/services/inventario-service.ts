import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Producto } from '../../shared/interfaces/producto';
import { TipoMovimiento } from '../../shared/interfaces/inventario';
import { AjusteInventario } from '../../shared/interfaces/inventario';
import { AjusteInventarioDTO } from '../../shared/interfaces/inventario';
import { MovimientoProducto } from '../../shared/interfaces/inventario';

@Injectable({
  providedIn: 'root',
})
export class InventarioService {
  private apiUrl = 'http://localhost:8080/inventario/api';

  constructor(private http: HttpClient) { }

  listarProductos(): Observable<Producto[]> {
    return this.http.get<{ success: boolean; data: Producto[] }>(`${this.apiUrl}/listar`)
      .pipe(map(res => res.data));
  }

  obtenerMovimientos(productoId: number): Observable<MovimientoProducto[]> {
    return this.http.get<{ success: boolean; data: MovimientoProducto[] }>(`${this.apiUrl}/movimientos/${productoId}`)
      .pipe(map(res => res.data));
  }

  obtenerAjustes(productoId: number): Observable<AjusteInventario[]> {
    return this.http.get<{ success: boolean; data: AjusteInventario[] }>(`${this.apiUrl}/ajustes/${productoId}`)
      .pipe(map(res => res.data));
  }

  guardarAjuste(dto: AjusteInventarioDTO): Observable<any> {
    return this.http.post(`${this.apiUrl}/guardarAjuste`, dto);
  }

  listarTiposMovimientos(): Observable<TipoMovimiento[]> {
    return this.http.get<{ success: boolean; data: TipoMovimiento[] }>(`${this.apiUrl}/tipoMovimientos`)
      .pipe(map(res => res.data));
  }
}
