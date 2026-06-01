import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth-service';

export const authGuard: CanActivateFn = (route) => {
    const auth = inject(AuthService);
    const router = inject(Router);

    if (!auth.isLoggedIn()) {
        router.navigate(['/login']);
        return false;
    }

    const opcionId = route.data?.['opcionId'];
    if (opcionId != null) {
        const opciones = auth.getOpciones();
        const tieneAcceso = opciones.some(op => op.id === opcionId);
        if (!tieneAcceso) {
            router.navigate(['/admin/dashboard']);
            return false;
        }
    }

    return true;
};