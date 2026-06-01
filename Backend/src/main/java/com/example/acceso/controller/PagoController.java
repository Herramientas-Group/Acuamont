package com.example.acceso.controller;

import com.example.acceso.DTO.PagosDTO;
import com.example.acceso.model.Venta;
import com.example.acceso.service.Interfaces.VentaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pagos")
public class PagoController {
    private final VentaService ventaService;
    public PagoController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping("/api/registrarPago")
    @PreAuthorize("hasAuthority('OPCION_8')")
    public ResponseEntity<Venta> registrarPago(@Valid @RequestBody PagosDTO pagoRequest) {
        Venta ventaActualizada = ventaService.registrarPago(pagoRequest);
        return ResponseEntity.ok(ventaActualizada);
    }
}
