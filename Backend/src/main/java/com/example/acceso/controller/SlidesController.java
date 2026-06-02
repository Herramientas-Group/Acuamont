package com.example.acceso.controller;

import com.cloudinary.utils.ObjectUtils;
import com.example.acceso.service.Implements.CloudinaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/slides")
public class SlidesController {

    private final CloudinaryService cloudinaryService;

    private final String CARPETA_SLIDES = "slides_inicio";

    public SlidesController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping("/api/listar-urls")
    public ResponseEntity<List<String>> obtenerSlidesJson() {
        try {
            List<String> slides = cloudinaryService.listarImagenesDeCarpeta(CARPETA_SLIDES);
            return ResponseEntity.ok(slides);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ArrayList<>());
        }
    }

    @PostMapping("/api/guardar")
    @PreAuthorize("hasAuthority('OPCION_6')")
    public ResponseEntity<?> subirSlide(@RequestParam("imagen") MultipartFile imagen) {
        try {
            String url = cloudinaryService.subirImagen(imagen, CARPETA_SLIDES);
            return ResponseEntity.ok(Map.of("success", true, "data", url));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Error al subir slide: " + e.getMessage()));
        }
    }

    @DeleteMapping("/api/eliminar/{nombre}")
    @PreAuthorize("hasAuthority('OPCION_6')")
    public ResponseEntity<?> eliminarSlide(@PathVariable String nombre) {
        try {
            int dotIndex = nombre.lastIndexOf('.');
            String publicId = CARPETA_SLIDES + "/" + (dotIndex > 0 ? nombre.substring(0, dotIndex) : nombre);
            cloudinaryService.eliminarImagen(publicId, ObjectUtils.emptyMap());
            return ResponseEntity.ok(Map.of("success", true, "message", "Slide eliminado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Error al eliminar slide: " + e.getMessage()));
        }
    }
}
