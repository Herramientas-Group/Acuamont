import { inject } from '@angular/core';
import { CanActivateChildFn, Router } from '@angular/router';
import { AuthService } from '../services/auth-service';

export const authGuard: CanActivateChildFn = (childRoute) => {
    const auth = inject(AuthService);
    const router = inject(Router);

    if (auth.isLoggedIn()) {
        const opcionId = childRoute.data?.['opcionId'];
        if (opcionId != null) {
            const opciones = auth.getOpciones();
            const tieneAcceso = opciones.some(op => op.id === opcionId);
            if (!tieneAcceso) {
                router.navigate(['/admin/dashboard']);
                return false;
            }
        }
        return true;
    }

    router.navigate(['/login']);
    return false;
};