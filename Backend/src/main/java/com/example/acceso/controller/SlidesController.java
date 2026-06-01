package com.example.acceso.controller;

import com.example.acceso.service.Implements.CloudinaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
}
