import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth-service';

const PUBLIC_API_PATHS = [
  '/redes/api/activas',
  '/comentarios/api/listar',
  '/comentarios/api/guardar',
  '/productos/api/listar',
  '/productos/api/categorias',
  '/categorias/api/activas',
  '/slides/api/listar-urls',
  '/api/auth/',
];

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (PUBLIC_API_PATHS.some(p => req.url.includes(p))) {
    return next(req);
  }

  const authService = inject(AuthService);
  const token = authService.getToken();

  const cloned = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(cloned).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        authService.logout();
      }
      return throwError(() => error);
    })
  );
};