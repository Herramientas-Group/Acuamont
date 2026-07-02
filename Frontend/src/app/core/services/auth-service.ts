import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { LoginResponse, Opcion } from '../../shared/interfaces/perfil';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = `${environment.apiUrl}/api/auth`;
  private opcionesSubject = new BehaviorSubject<Opcion[]>(this.cargarOpcionesStorage());

  opciones$: Observable<Opcion[]> = this.opcionesSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) { }

  login(credentials: { usuario: string, clave: string, token?: string }) {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials)
      .pipe(
        tap(res => {
          if (res.token) {
            localStorage.setItem('token', res.token);
            localStorage.setItem('nombreUsuario', res.nombre);
            localStorage.setItem('perfil', res.perfil);
            localStorage.setItem('usuario', res.usuario);
            localStorage.setItem('usuarioId', String(res.id));
            localStorage.setItem('opciones', JSON.stringify(res.opciones));
            this.opcionesSubject.next(res.opciones);
          }
        })
      );
  }

  getUsuarioId(): number | null {
    const id = localStorage.getItem('usuarioId');
    return id ? Number(id) : null;
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getNombre(): string | null {
    return localStorage.getItem('nombreUsuario');
  }

  getPerfil(): string | null {
    return localStorage.getItem('perfil');
  }

  getOpciones(): Opcion[] {
    return this.opcionesSubject.getValue();
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    localStorage.clear();
    this.opcionesSubject.next([]);
    this.router.navigate(['/login']);
  }

  private cargarOpcionesStorage(): Opcion[] {
    const stored = localStorage.getItem('opciones');
    if (stored) {
      try {
        return JSON.parse(stored);
      } catch {
        return [];
      }
    }
    return [];
  }
}