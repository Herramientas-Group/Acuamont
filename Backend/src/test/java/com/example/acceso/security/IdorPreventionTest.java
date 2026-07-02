package com.example.acceso.security;

import com.example.acceso.controller.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class IdorPreventionTest {

    @Test
    void usuarioController_endpointsEscrituraTienenAutorizacion() {
        for (Method method : UsuarioController.class.getDeclaredMethods()) {
            if (method.isSynthetic()) continue;
            if (isWriteOperation(method.getName())) {
                assertTrue(
                    method.isAnnotationPresent(PreAuthorize.class),
                    "UsuarioController." + method.getName()
                        + " no tiene @PreAuthorize"
                );
            }
        }
    }

    @Test
    void clienteController_endpointsEscrituraTienenAutorizacion() {
        for (Method method : ClienteController.class.getDeclaredMethods()) {
            if (method.isSynthetic()) continue;
            if (isWriteOperation(method.getName())) {
                assertTrue(
                    method.isAnnotationPresent(PreAuthorize.class),
                    "ClienteController." + method.getName()
                        + " no tiene @PreAuthorize"
                );
            }
        }
    }

    @Test
    void productoController_endpointsEscrituraTienenAutorizacion() {
        for (Method method : ProductoController.class.getDeclaredMethods()) {
            if (method.isSynthetic()) continue;
            if (isWriteOperation(method.getName())) {
                assertTrue(
                    method.isAnnotationPresent(PreAuthorize.class),
                    "ProductoController." + method.getName()
                        + " no tiene @PreAuthorize"
                );
            }
        }
    }

    @Test
    void ventaController_endpointsEscrituraTienenAutorizacion() {
        for (Method method : VentaController.class.getDeclaredMethods()) {
            if (method.isSynthetic()) continue;
            if (isWriteOperation(method.getName())) {
                assertTrue(
                    method.isAnnotationPresent(PreAuthorize.class),
                    "VentaController." + method.getName()
                        + " no tiene @PreAuthorize"
                );
            }
        }
    }

    @Test
    void categoriaController_endpointsEscrituraTienenAutorizacion() {
        for (Method method : CategoriaController.class.getDeclaredMethods()) {
            if (method.isSynthetic()) continue;
            if (isWriteOperation(method.getName())) {
                assertTrue(
                    method.isAnnotationPresent(PreAuthorize.class),
                    "CategoriaController." + method.getName()
                        + " no tiene @PreAuthorize"
                );
            }
        }
    }

    @Test
    void proveedorController_endpointsEscrituraTienenAutorizacion() {
        for (Method method : ProveedorController.class.getDeclaredMethods()) {
            if (method.isSynthetic()) continue;
            if (isWriteOperation(method.getName())) {
                assertTrue(
                    method.isAnnotationPresent(PreAuthorize.class),
                    "ProveedorController." + method.getName()
                        + " no tiene @PreAuthorize"
                );
            }
        }
    }

    @Test
    void perfilController_endpointsEscrituraTienenAutorizacion() {
        for (Method method : PerfilController.class.getDeclaredMethods()) {
            if (method.isSynthetic()) continue;
            if (isWriteOperation(method.getName())) {
                assertTrue(
                    method.isAnnotationPresent(PreAuthorize.class),
                    "PerfilController." + method.getName()
                        + " no tiene @PreAuthorize"
                );
            }
        }
    }

    @Test
    void inventarioController_endpointsEscrituraTienenAutorizacion() {
        for (Method method : InventarioController.class.getDeclaredMethods()) {
            if (method.isSynthetic()) continue;
            if (isWriteOperation(method.getName())) {
                assertTrue(
                    method.isAnnotationPresent(PreAuthorize.class),
                    "InventarioController." + method.getName()
                        + " no tiene @PreAuthorize"
                );
            }
        }
    }

    private boolean isWriteOperation(String methodName) {
        return methodName.contains("guardar")
            || methodName.contains("eliminar")
            || methodName.contains("cambiarEstado")
            || methodName.contains("actualizar")
            || methodName.contains("crear")
            || methodName.contains("registrar")
            || methodName.contains("activar")
            || methodName.contains("desactivar");
    }
}
