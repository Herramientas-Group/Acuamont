import { TestBed } from '@angular/core/testing';
import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpErrorResponse } from '@angular/common/http';
import { provideHttpClient, HttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';

import { authInterceptor } from './auth-interceptor';
import { AuthService } from '../services/auth-service';

describe('authInterceptor - Token Security', () => {
  let httpMock: HttpTestingController;
  let httpClient: HttpClient;
  let mockAuthService: jasmine.SpyObj<AuthService>;

  beforeEach(() => {
    mockAuthService = jasmine.createSpyObj('AuthService', ['getToken', 'logout']);
    mockAuthService.getToken.and.returnValue('test-jwt-token');

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: AuthService, useValue: mockAuthService },
      ],
    });

    httpMock = TestBed.inject(HttpTestingController);
    httpClient = TestBed.inject(HttpClient);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    const interceptor: HttpInterceptorFn = (req, next) =>
      TestBed.runInInjectionContext(() => authInterceptor(req, next));
    expect(interceptor).toBeTruthy();
  });

  describe('Token Injection', () => {
    it('should attach Bearer token to non-public requests', () => {
      httpClient.get('http://localhost:8080/usuarios/api').subscribe();

      const req = httpMock.expectOne('http://localhost:8080/usuarios/api');
      expect(req.request.headers.get('Authorization')).toBe('Bearer test-jwt-token');
      req.flush({});
    });

    it('should not attach token to auth endpoint', () => {
      httpClient.post('http://localhost:8080/api/auth/login', {}).subscribe();

      const req = httpMock.expectOne('http://localhost:8080/api/auth/login');
      expect(req.request.headers.has('Authorization')).toBeFalse();
      req.flush({});
    });

    it('should not attach token to public API paths', () => {
      httpClient.get('http://localhost:8080/comentarios/api/listar').subscribe();

      const req = httpMock.expectOne('http://localhost:8080/comentarios/api/listar');
      expect(req.request.headers.has('Authorization')).toBeFalse();
      req.flush({});
    });

    it('should not attach token when no token is stored', () => {
      mockAuthService.getToken.and.returnValue(null);

      httpClient.get('http://localhost:8080/productos/api/listar').subscribe();

      const req = httpMock.expectOne('http://localhost:8080/productos/api/listar');
      expect(req.request.headers.has('Authorization')).toBeFalse();
      req.flush({});
    });
  });

  describe('Error Handling', () => {
    it('should call logout on 401 response', () => {
      httpClient.get('http://localhost:8080/usuarios/api').subscribe({
        error: () => {},
      });

      const req = httpMock.expectOne('http://localhost:8080/usuarios/api');
      req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

      expect(mockAuthService.logout).toHaveBeenCalled();
    });

    it('should not call logout on non-401 errors', () => {
      httpClient.get('http://localhost:8080/usuarios/api').subscribe({
        error: () => {},
      });

      const req = httpMock.expectOne('http://localhost:8080/usuarios/api');
      req.flush('Forbidden', { status: 403, statusText: 'Forbidden' });

      expect(mockAuthService.logout).not.toHaveBeenCalled();
    });
  });

  describe('Security: Token Header Format', () => {
    it('should format Bearer token correctly', () => {
      httpClient.get('http://localhost:8080/ventas/api').subscribe();

      const req = httpMock.expectOne('http://localhost:8080/ventas/api');
      const authHeader = req.request.headers.get('Authorization');
      expect(authHeader).toMatch(/^Bearer\s.+/);
      expect(authHeader).toBe('Bearer test-jwt-token');
      req.flush({});
    });

    it('should not expose token in URL', () => {
      httpClient.get('http://localhost:8080/ventas/api').subscribe();

      const req = httpMock.expectOne('http://localhost:8080/ventas/api');
      expect(req.request.url).not.toContain('token');
      expect(req.request.url).not.toContain('Bearer');
      expect(req.request.url).not.toContain('test-jwt-token');
      req.flush({});
    });
  });
});
