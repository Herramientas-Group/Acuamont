package com.example.acceso.controller;

import com.example.acceso.DTO.RedSocialDTO;
import com.example.acceso.model.RedSocial;
import com.example.acceso.service.Interfaces.RedSocialService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/redes")
public class RedesSocialesController {
    private final RedSocialService redSocialService;
    public RedesSocialesController(RedSocialService redSocialService) {
        this.redSocialService = redSocialService;
    }

    @GetMapping("/api/activas")
    public ResponseEntity<?> listarRedesActivas() {
        return ResponseEntity.ok(redSocialService.listarRedesSocialesActivas());
    }

    @GetMapping("/api/listar")
    @ResponseBody
    @PreAuthorize("hasAuthority('OPCION_6')")
    public ResponseEntity<?> listarRedesSocialesApi() {
        Map<String, Object> response = new HashMap<>();
        List<RedSocial> redesSociales = redSocialService.listarRedesSociales();
        response.put("success", true);
        response.put("data", redesSociales);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/actualizar/{id}")
    @ResponseBody
    @PreAuthorize("hasAuthority('OPCION_6')")
    public ResponseEntity<?> actualizarRedSocial(@PathVariable Long id, @RequestBody RedSocialDTO redSocial) {
        RedSocial redSocialActualizada = redSocialService.actualizarRedSocial(id, redSocial);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Red social actualizada correctamente");
        response.put("data", redSocialActualizada);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/cambiar-estado/{id}")
    @ResponseBody
    @PreAuthorize("hasAuthority('OPCION_6')")
    public ResponseEntity<?> cambiarEstadoRedSocial(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        return redSocialService.cambiarEstadoRedSocial(id)
                .map(redSocial -> {
                    response.put("success", true);
                    response.put("message", "Estado de la red social actualizado correctamente");
                    response.put("data", redSocial);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    response.put("success", false);
                    response.put("message", "Red social no encontrada");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }
}
