import { TestBed } from '@angular/core/testing';
import { Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { authGuard } from './auth.guard';
import { AuthService } from '../services/auth-service';

describe('authGuard - Route Protection', () => {
  let mockRouter: jasmine.SpyObj<Router>;
  let mockAuthService: jasmine.SpyObj<AuthService>;

  const createMockRoute = (opcionId?: number): ActivatedRouteSnapshot => {
    const route = { data: {} } as ActivatedRouteSnapshot;
    if (opcionId !== undefined) {
      route.data = { opcionId };
    }
    return route;
  };

  beforeEach(() => {
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);
    mockAuthService = jasmine.createSpyObj('AuthService', ['isLoggedIn', 'getOpciones']);

    TestBed.configureTestingModule({
      providers: [
        { provide: Router, useValue: mockRouter },
        { provide: AuthService, useValue: mockAuthService },
      ],
    });
  });

  describe('Authentication Check', () => {
    it('should allow access when user is logged in', () => {
      mockAuthService.isLoggedIn.and.returnValue(true);
      mockAuthService.getOpciones.and.returnValue([]);

      const result = TestBed.runInInjectionContext(() => {
        return authGuard(createMockRoute(), {} as RouterStateSnapshot);
      });

      expect(result).toBeTrue();
      expect(mockRouter.navigate).not.toHaveBeenCalled();
    });

    it('should redirect to login when user is not logged in', () => {
      mockAuthService.isLoggedIn.and.returnValue(false);

      const result = TestBed.runInInjectionContext(() => {
        return authGuard(createMockRoute(), {} as RouterStateSnapshot);
      });

      expect(result).toBeFalse();
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/login']);
    });
  });

  describe('Authorization Check (RBAC)', () => {
    it('should allow access when user has required permission', () => {
      mockAuthService.isLoggedIn.and.returnValue(true);
      mockAuthService.getOpciones.and.returnValue([{ id: 2, nombre: 'Clientes' }]);

      const result = TestBed.runInInjectionContext(() => {
        return authGuard(createMockRoute(2), {} as RouterStateSnapshot);
      });

      expect(result).toBeTrue();
      expect(mockRouter.navigate).not.toHaveBeenCalled();
    });

    it('should deny access when user lacks required permission', () => {
      mockAuthService.isLoggedIn.and.returnValue(true);
      mockAuthService.getOpciones.and.returnValue([{ id: 1, nombre: 'Dashboard' }]);

      const result = TestBed.runInInjectionContext(() => {
        return authGuard(createMockRoute(2), {} as RouterStateSnapshot);
      });

      expect(result).toBeFalse();
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/admin/dashboard']);
    });

    it('should allow access when no opcionId is required', () => {
      mockAuthService.isLoggedIn.and.returnValue(true);
      mockAuthService.getOpciones.and.returnValue([]);

      const result = TestBed.runInInjectionContext(() => {
        return authGuard(createMockRoute(), {} as RouterStateSnapshot);
      });

      expect(result).toBeTrue();
      expect(mockRouter.navigate).not.toHaveBeenCalled();
    });

    it('should handle multiple permissions correctly', () => {
      mockAuthService.isLoggedIn.and.returnValue(true);
      mockAuthService.getOpciones.and.returnValue([
        { id: 1, nombre: 'Dashboard' },
        { id: 3, nombre: 'Productos' },
      ]);

      const result = TestBed.runInInjectionContext(() => {
        return authGuard(createMockRoute(3), {} as RouterStateSnapshot);
      });

      expect(result).toBeTrue();
    });
  });

  describe('Edge Cases', () => {
    it('should handle empty route data', () => {
      mockAuthService.isLoggedIn.and.returnValue(true);
      mockAuthService.getOpciones.and.returnValue([]);

      const route = { data: null } as unknown as ActivatedRouteSnapshot;

      const result = TestBed.runInInjectionContext(() => {
        return authGuard(route, {} as RouterStateSnapshot);
      });

      expect(result).toBeTrue();
    });

    it('should reject when auth service throws', () => {
      mockAuthService.isLoggedIn.and.throwError('Auth service error');

      expect(() => {
        TestBed.runInInjectionContext(() => {
          return authGuard(createMockRoute(), {} as RouterStateSnapshot);
        });
      }).toThrow();
    });
  });
});
