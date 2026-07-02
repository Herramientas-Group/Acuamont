import { TestBed } from '@angular/core/testing';
import { Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { authGuard } from './auth.guard';
import { AuthService } from '../services/auth-service';

describe('authGuard - Route Protection', () => {
  let mockRouter: { navigate: ReturnType<typeof vi.fn> };
  let mockAuthService: { isLoggedIn: ReturnType<typeof vi.fn>; getOpciones: ReturnType<typeof vi.fn> };

  const createMockRoute = (opcionId?: number): ActivatedRouteSnapshot => {
    const route = { data: {} } as ActivatedRouteSnapshot;
    if (opcionId !== undefined) {
      route.data = { opcionId };
    }
    return route;
  };

  beforeEach(() => {
    mockRouter = { navigate: vi.fn() };
    mockAuthService = { isLoggedIn: vi.fn(), getOpciones: vi.fn() };

    TestBed.configureTestingModule({
      providers: [
        { provide: Router, useValue: mockRouter },
        { provide: AuthService, useValue: mockAuthService },
      ],
    });
  });

  describe('Authentication Check', () => {
    it('should allow access when user is logged in', () => {
      mockAuthService.isLoggedIn.mockReturnValue(true);
      mockAuthService.getOpciones.mockReturnValue([]);

      const result = TestBed.runInInjectionContext(() => {
        return authGuard(createMockRoute(), {} as RouterStateSnapshot);
      });

      expect(result).toBe(true);
      expect(mockRouter.navigate).not.toHaveBeenCalled();
    });

    it('should redirect to login when user is not logged in', () => {
      mockAuthService.isLoggedIn.mockReturnValue(false);

      const result = TestBed.runInInjectionContext(() => {
        return authGuard(createMockRoute(), {} as RouterStateSnapshot);
      });

      expect(result).toBe(false);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/login']);
    });
  });

  describe('Authorization Check (RBAC)', () => {
    it('should allow access when user has required permission', () => {
      mockAuthService.isLoggedIn.mockReturnValue(true);
      mockAuthService.getOpciones.mockReturnValue([{ id: 2, nombre: 'Clientes' }]);

      const result = TestBed.runInInjectionContext(() => {
        return authGuard(createMockRoute(2), {} as RouterStateSnapshot);
      });

      expect(result).toBe(true);
      expect(mockRouter.navigate).not.toHaveBeenCalled();
    });

    it('should deny access when user lacks required permission', () => {
      mockAuthService.isLoggedIn.mockReturnValue(true);
      mockAuthService.getOpciones.mockReturnValue([{ id: 1, nombre: 'Dashboard' }]);

      const result = TestBed.runInInjectionContext(() => {
        return authGuard(createMockRoute(2), {} as RouterStateSnapshot);
      });

      expect(result).toBe(false);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/admin/dashboard']);
    });

    it('should allow access when no opcionId is required', () => {
      mockAuthService.isLoggedIn.mockReturnValue(true);
      mockAuthService.getOpciones.mockReturnValue([]);

      const result = TestBed.runInInjectionContext(() => {
        return authGuard(createMockRoute(), {} as RouterStateSnapshot);
      });

      expect(result).toBe(true);
      expect(mockRouter.navigate).not.toHaveBeenCalled();
    });

    it('should handle multiple permissions correctly', () => {
      mockAuthService.isLoggedIn.mockReturnValue(true);
      mockAuthService.getOpciones.mockReturnValue([
        { id: 1, nombre: 'Dashboard' },
        { id: 3, nombre: 'Productos' },
      ]);

      const result = TestBed.runInInjectionContext(() => {
        return authGuard(createMockRoute(3), {} as RouterStateSnapshot);
      });

      expect(result).toBe(true);
    });
  });

  describe('Edge Cases', () => {
    it('should handle empty route data', () => {
      mockAuthService.isLoggedIn.mockReturnValue(true);
      mockAuthService.getOpciones.mockReturnValue([]);

      const route = { data: null } as unknown as ActivatedRouteSnapshot;

      const result = TestBed.runInInjectionContext(() => {
        return authGuard(route, {} as RouterStateSnapshot);
      });

      expect(result).toBe(true);
    });

    it('should reject when auth service throws', () => {
      mockAuthService.isLoggedIn.mockImplementation(() => { throw new Error('Auth service error'); });

      expect(() => {
        TestBed.runInInjectionContext(() => {
          return authGuard(createMockRoute(), {} as RouterStateSnapshot);
        });
      }).toThrow();
    });
  });
});
