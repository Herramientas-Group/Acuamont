package com.example.acceso.controller;

import com.example.acceso.service.Implements.CloudinaryService;
import com.example.acceso.service.Interfaces.ComentarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/comentarios")
@RequiredArgsConstructor
public class ComentarioController {

    private final ComentarioService comentarioService;
    private final CloudinaryService cloudinaryService;

    @GetMapping("/api/listar")
    public ResponseEntity<?> listarComentarios() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", comentarioService.listarComentarios());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/guardar")
    public ResponseEntity<?> guardarComentario(
            @RequestParam("nombre") String nombre,
            @RequestParam("mensaje") String mensaje,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {

        Map<String, Object> response = new HashMap<>();
        try {
            String imagenUrl = null;
            if (imagen != null && !imagen.isEmpty()) {
                imagenUrl = cloudinaryService.subirImagen(imagen, "comentarios_acuamont");
            }

            var dto = comentarioService.guardarComentario(nombre, mensaje, imagenUrl);
            response.put("success", true);
            response.put("data", dto);
            response.put("message", "Comentario agregado exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al guardar el comentario: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
