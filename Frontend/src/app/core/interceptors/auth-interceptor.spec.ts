import { TestBed } from '@angular/core/testing';
import { HttpInterceptorFn, withInterceptors } from '@angular/common/http';
import { provideHttpClient, HttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';

import { authInterceptor } from './auth-interceptor';
import { AuthService } from '../services/auth-service';
import { environment } from '../../../environments/environment';

describe('authInterceptor - Token Security', () => {
  let httpMock: HttpTestingController;
  let httpClient: HttpClient;
  let mockAuthService: { getToken: ReturnType<typeof vi.fn>; logout: ReturnType<typeof vi.fn> };

  beforeEach(() => {
    mockAuthService = { getToken: vi.fn(), logout: vi.fn() };
    mockAuthService.getToken.mockReturnValue('test-jwt-token');

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
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
      httpClient.get(`${environment.apiUrl}/usuarios/api`).subscribe();

      const req = httpMock.expectOne(`${environment.apiUrl}/usuarios/api`);
      expect(req.request.headers.get('Authorization')).toBe('Bearer test-jwt-token');
      req.flush({});
    });

    it('should not attach token to auth endpoint', () => {
      httpClient.post(`${environment.apiUrl}/api/auth/login`, {}).subscribe();

      const req = httpMock.expectOne(`${environment.apiUrl}/api/auth/login`);
      expect(req.request.headers.has('Authorization')).toBe(false);
      req.flush({});
    });

    it('should not attach token to public API paths', () => {
      httpClient.get(`${environment.apiUrl}/comentarios/api/listar`).subscribe();

      const req = httpMock.expectOne(`${environment.apiUrl}/comentarios/api/listar`);
      expect(req.request.headers.has('Authorization')).toBe(false);
      req.flush({});
    });

    it('should not attach token when no token is stored', () => {
      mockAuthService.getToken.mockReturnValue(null);

      httpClient.get(`${environment.apiUrl}/productos/api/listar`).subscribe();

      const req = httpMock.expectOne(`${environment.apiUrl}/productos/api/listar`);
      expect(req.request.headers.has('Authorization')).toBe(false);
      req.flush({});
    });
  });

  describe('Error Handling', () => {
    it('should call logout on 401 response', () => {
      httpClient.get(`${environment.apiUrl}/usuarios/api`).subscribe({
        error: () => {},
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/usuarios/api`);
      req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

      expect(mockAuthService.logout).toHaveBeenCalled();
    });

    it('should not call logout on non-401 errors', () => {
      httpClient.get(`${environment.apiUrl}/usuarios/api`).subscribe({
        error: () => {},
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/usuarios/api`);
      req.flush('Forbidden', { status: 403, statusText: 'Forbidden' });

      expect(mockAuthService.logout).not.toHaveBeenCalled();
    });
  });

  describe('Security: Token Header Format', () => {
    it('should format Bearer token correctly', () => {
      httpClient.get(`${environment.apiUrl}/ventas/api`).subscribe();

      const req = httpMock.expectOne(`${environment.apiUrl}/ventas/api`);
      const authHeader = req.request.headers.get('Authorization');
      expect(authHeader).toMatch(/^Bearer\s.+/);
      expect(authHeader).toBe('Bearer test-jwt-token');
      req.flush({});
    });

    it('should not expose token in URL', () => {
      httpClient.get(`${environment.apiUrl}/ventas/api`).subscribe();

      const req = httpMock.expectOne(`${environment.apiUrl}/ventas/api`);
      expect(req.request.url).not.toContain('token');
      expect(req.request.url).not.toContain('Bearer');
      expect(req.request.url).not.toContain('test-jwt-token');
      req.flush({});
    });
  });
});
