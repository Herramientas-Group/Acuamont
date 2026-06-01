package com.example.acceso.controller;

import com.example.acceso.model.Categoria;
import com.example.acceso.service.Interfaces.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {
    private final CategoriaService categoriaService;
    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping("/api/listar")
    @PreAuthorize("hasAuthority('OPCION_4')")
    public ResponseEntity<?> listarCategoriasApi() {
        Map<String, Object> response = new HashMap<>();
        List<Categoria> categorias = categoriaService.listarCategorias();
        response.put("success", true);
        response.put("data", categorias);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/guardar")
    @PreAuthorize("hasAuthority('OPCION_4')")
    public ResponseEntity<?> guardarCategoriaAjax(@Valid @RequestBody Categoria categoria, BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();
        if(bindingResult.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));
            response.put("success", false);
            response.put("message", "Datos inválidos");
            response.put("errors", errores);
            return ResponseEntity.badRequest().body(response);
        }
        try {
            Categoria categoriaGuardada = categoriaService.guardarCategoria(categoria);
            response.put("success", true);
            response.put("categoria", categoriaGuardada);
            response.put("message",
                    categoria.getId() != null ? "Categoria actualizada correctamente" : "Categoria creada correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al guardar la categoria: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/api/{id}")
    @PreAuthorize("hasAuthority('OPCION_4')")
    public ResponseEntity<?> obtenerCategoria(@PathVariable Long id) {
        try {
            return categoriaService.obtenerCategoriaPorId(id).map(categoria -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", categoria);
                return ResponseEntity.ok(response);
            }).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al obtener la categoria: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/api/eliminar/{id}")
    @PreAuthorize("hasAuthority('OPCION_4')")
    public ResponseEntity<?> eliminarCategoriaAjax(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!categoriaService.obtenerCategoriaPorId(id).isPresent()) {
                response.put("success", false);
                response.put("message", "Categoria no encontrada");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            categoriaService.eliminarCategoria(id);
            response.put("success", true);
            response.put("message", "Categoria eliminada correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar la categoria: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/api/cambiar-estado/{id}")
    @PreAuthorize("hasAuthority('OPCION_4')")
    public ResponseEntity<?> cambiarEstadoCategoriaAjax(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            return categoriaService.cambiarEstadoCategoria(id)
                .map(categoria -> {
                    response.put("success", true);
                    response.put("categoria", categoria);
                    response.put("message", "Estado de la categoria actualizado correctamente");
                    return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                response.put("success", false);
                response.put("message", "Categoria no encontrada");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            });
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al cambiar el estado de la categoria: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
