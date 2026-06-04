import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Venta, SerieComprobante, FormaPago, Cuota, Pago, VentaDTO, PagosDTO } from '../../shared/interfaces/venta';

@Injectable({
  providedIn: 'root',
})
export class VentasService {
  private apiUrl = 'http://localhost:8080/ventas/api';
  private pagosUrl = 'http://localhost:8080/pagos/api';

  constructor(private http: HttpClient) { }

  listarVentas(): Observable<Venta[]> {
    return this.http.get<{ success: boolean, data: Venta[] }>(`${this.apiUrl}/listar`)
      .pipe(map(res => res.data));
  }

  obtenerVenta(id: number): Observable<{ success: boolean, data: Venta }> {
    return this.http.get<{ success: boolean, data: Venta }>(`${this.apiUrl}/ventas_id/${id}`);
  }

  listarFormasPago(): Observable<FormaPago[]> {
    return this.http.get<{ success: boolean, data: FormaPago[] }>(`${this.apiUrl}/formaPago`)
      .pipe(map(res => res.data));
  }

  listarSeriesComprobante(): Observable<SerieComprobante[]> {
    return this.http.get<{ success: boolean, data: SerieComprobante[] }>(`${this.apiUrl}/serieComprobante`)
      .pipe(map(res => res.data));
  }

  guardarVenta(ventaDTO: VentaDTO): Observable<any> {
    return this.http.post(`${this.apiUrl}/guardar`, ventaDTO);
  }

  actualizarVenta(id: number, ventaDTO: VentaDTO): Observable<any> {
    return this.http.put(`${this.apiUrl}/actualizar/${id}`, ventaDTO);
  }

  eliminarVenta(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/eliminar/${id}`);
  }

  listarCuotas(ventaId: number): Observable<Cuota[]> {
    return this.http.get<Cuota[]>(`${this.apiUrl}/cuotas/${ventaId}`);
  }

  listarPagos(ventaId: number): Observable<Pago[]> {
    return this.http.get<Pago[]>(`${this.apiUrl}/pagos/${ventaId}`);
  }

  descargarBoleta(ventaId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/boleta/${ventaId}`, { responseType: 'blob' });
  }

  enviarBoletaCorreo(ventaId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/envio-correo/${ventaId}`, {});
  }

  registrarPago(pagosDTO: PagosDTO): Observable<any> {
    return this.http.post(`${this.pagosUrl}/registrarPago`, pagosDTO);
  }
}
