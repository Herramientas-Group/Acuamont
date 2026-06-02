package com.example.acceso.controller;

import com.example.acceso.service.Implements.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/logo")
@RequiredArgsConstructor
public class LogoController {

    private final CloudinaryService cloudinaryService;

    @GetMapping("/api/url")
    @PreAuthorize("hasAuthority('OPCION_6')")
    public ResponseEntity<?> obtenerLogoUrl() {
        Map<String, Object> response = new HashMap<>();
        String url = cloudinaryService.obtenerUrlImagen("logo2");
        response.put("success", true);
        response.put("data", url != null ? url : "");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/guardar")
    @PreAuthorize("hasAuthority('OPCION_6')")
    public ResponseEntity<?> subirLogo(@RequestParam("logo") MultipartFile logo) {
        Map<String, Object> response = new HashMap<>();
        try {
            String url = cloudinaryService.subirImagenConNombreFijo(logo, "logo2");
            response.put("success", true);
            response.put("data", url);
            response.put("message", "Logo actualizado correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al subir el logo: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
