package com.example.acceso.controller;

import com.example.acceso.DTO.ReporteUtilidadProductoDTO;
import com.example.acceso.DTO.ReporteUtilidadVentaDTO;
import com.example.acceso.DTO.ReporteUttilidadUsuarioDTO;
import com.example.acceso.service.Interfaces.ReportesService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reportes")
public class ReportesController {

    private final ReportesService reportesService;

    public ReportesController(ReportesService reportesService) {
        this.reportesService = reportesService;
    }

    @GetMapping("/api/utilidad-ventas")
    @PreAuthorize("hasAuthority('OPCION_11')")
    public ResponseEntity<?> getUtilidadVentas() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<ReporteUtilidadVentaDTO> reporte = reportesService.obtenerUtilidadPorVenta();
            response.put("success", true);
            response.put("data", reporte);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al cargar el reporte: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/api/utilidad-ventas-rango")
    @PreAuthorize("hasAuthority('OPCION_11')")
    public ResponseEntity<?> getUtilidadVentasRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            LocalDateTime fechaInicio = inicio.atStartOfDay();
            LocalDateTime fechaFin = fin.atTime(LocalTime.MAX);
            List<ReporteUtilidadVentaDTO> reporte = reportesService.obtenerUtilidadPorVentaRango(fechaInicio, fechaFin);
            response.put("success",true);
            response.put("data",reporte);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success",false);
            response.put("message",e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/api/utilidad-usuarios")
    @PreAuthorize("hasAuthority('OPCION_11')")
    public ResponseEntity<?> getUtilidadUsuarios() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<ReporteUttilidadUsuarioDTO> reporte = reportesService.obtenerUtilidadPorUsuario();
            response.put("success",true);
            response.put("data",reporte);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success",false);
            response.put("message",e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/api/utilidad-usuarios-rango")
    @PreAuthorize("hasAuthority('OPCION_11')")
    public ResponseEntity<?> getUtilidadUsuariosRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            LocalDateTime fechaInicio = inicio.atStartOfDay();
            LocalDateTime fechaFin = fin.atTime(LocalTime.MAX);
            List<ReporteUttilidadUsuarioDTO> reporte = reportesService.obtenerUtilidadPorUsuarioRango(fechaInicio, fechaFin);
            response.put("success",true);
            response.put("data",reporte);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success",false);
            response.put("message",e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/api/utilidad-producto")
    @PreAuthorize("hasAuthority('OPCION_11')")
    public ResponseEntity<?> getUtilidadProductos() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<ReporteUtilidadProductoDTO> reporte = reportesService.obtenerUtilidadPorProducto();
            response.put("success",true);
            response.put("data",reporte);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success",false);
            response.put("message",e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/api/utilidad-producto-rango")
    @PreAuthorize("hasAuthority('OPCION_11')")
    public ResponseEntity<?> getUtilidadProductosRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            LocalDateTime fechaInicio = inicio.atStartOfDay();
            LocalDateTime fechaFin = fin.atTime(LocalTime.MAX);
            List<ReporteUtilidadProductoDTO> reporte = reportesService.obtenerUtilidadPorProductoRango(fechaInicio, fechaFin);
            response.put("success",true);
            response.put("data",reporte);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success",false);
            response.put("message",e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

}
