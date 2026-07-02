import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Usuario, Perfil, Verificar2FARequest } from '../../shared/interfaces/perfil';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class UsuarioService {
  private apiUrl = `${environment.apiUrl}/usuarios/api`;

  constructor(private http: HttpClient) { }

  listarUsuarios(): Observable<Usuario[]> {
    return this.http.get<any>(`${this.apiUrl}/listar`, { withCredentials: true })
      .pipe(map(response => response.data));
  }

  listarPerfiles(): Observable<Perfil[]> {
    return this.http.get<any>(`${this.apiUrl}/perfiles`)
      .pipe(map(response => response.data || response));
  }

  obtenerUsuario(id: number): Observable<Usuario> {
    return this.http.get<any>(`${this.apiUrl}/${id}`)
      .pipe(map(response => response.data || response));
  }

  guardarUsuario(usuario: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/guardar`, usuario);
  }

  eliminarUsuario(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/eliminar/${id}`);
  }

  cambiarEstado(id: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/cambiar-estado/${id}`, {});
  }

  generar2FA(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/generar-2fa/${id}`);
  }

  verificar2FA(data: Verificar2FARequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/verificar-2fa`, data);
  }
}
