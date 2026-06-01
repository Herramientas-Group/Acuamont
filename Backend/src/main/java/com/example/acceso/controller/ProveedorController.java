package com.example.acceso.controller;

import com.example.acceso.model.Proveedor;
import com.example.acceso.service.Interfaces.ProveedorService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }
    
    @GetMapping("/api/listar")
    @PreAuthorize("hasAuthority('OPCION_10')")
    public ResponseEntity<?> listarProveedoresApi() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", proveedorService.listarProveedores());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/guardar")
    @PreAuthorize("hasAuthority('OPCION_10')")
    public ResponseEntity<?> guardarProveedorApi(@Valid @RequestBody Proveedor proveedor, BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();

        if(bindingResult.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));
            response.put("success", false);
            response.put("message", "Datos inválidos");
            response.put("errors", errores);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Proveedor proveedorGuardado = proveedorService.guardarProveedor(proveedor);
            response.put("success", true);
            response.put("proveedor", proveedorGuardado);
            response.put("message",
                    proveedor.getId() != null ? "Proveedor actualizado correctamente" : "Proveedor creado correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al guardar el proveedor: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/api/cambiar-estado/{id}")
    @PreAuthorize("hasAuthority('OPCION_10')")
    public ResponseEntity<?> cambiarEstadoProveedorAjax(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            return proveedorService.cambiarEstadoProveedor(id)
                    .map(proveedor -> {
                        response.put("success", true);
                        response.put("data", proveedor);
                        response.put("message", "Estado del proveedor actualizado correctamente");
                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(()->{
                        response.put("success", false);
                        response.put("message", "Proveedor no encontrado");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                    });
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al cambiar el estado del proveedor: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/api/{id}")
    @PreAuthorize("hasAuthority('OPCION_10')")
    public ResponseEntity<?> obtenerProveedor(@PathVariable Long id) {
        try {
            return proveedorService.obtenerProveedorPorId(id).map(proveedor -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", proveedor);
                return ResponseEntity.ok(response);
            }).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al obtener el proveedor: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/api/eliminar/{id}")
    @PreAuthorize("hasAuthority('OPCION_10')")
    public ResponseEntity<?> eliminarProveedor(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!proveedorService.obtenerProveedorPorId(id).isPresent()) {
                response.put("success", false);
                response.put("message", "Proveedor no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            proveedorService.eliminarProveedor(id);
            response.put("success", true);
            response.put("message", "Proveedor eliminado correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar el proveedor: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }


    @GetMapping("/api/buscar-proveedor-documento/{documento}")
    @PreAuthorize("hasAuthority('OPCION_10')")
    public ResponseEntity<?> buscarPorDocumentoInterno(@PathVariable String documento) {
        return proveedorService.obtenerProveedorPorDocumento(documento)
                .map(proveedor -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("data", proveedor);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "Proveedor no encontrado en la base de datos local.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }
}
