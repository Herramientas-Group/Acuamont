package com.example.acceso.controller;

import com.example.acceso.DTO.VentaDTO;
import com.example.acceso.model.Cuota;
import com.example.acceso.model.Pago;
import com.example.acceso.model.Venta;
import com.example.acceso.service.Interfaces.FormaPagoService;
import com.example.acceso.service.Interfaces.GenerarBoletaService;
import com.example.acceso.service.Interfaces.SerieComprobanteService;
import com.example.acceso.service.Interfaces.VentaService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/ventas")
public class VentaController {
    private GenerarBoletaService generarBoletaService;
    private final VentaService ventaService;
    private final FormaPagoService formaPagoService;
    private final SerieComprobanteService serieComprobanteService;
    public VentaController(VentaService ventaService, FormaPagoService formaPagoService,
                           SerieComprobanteService serieComprobanteService,GenerarBoletaService generarBoletaService) {
        this.generarBoletaService = generarBoletaService;
        this.ventaService = ventaService;
        this.formaPagoService = formaPagoService;
        this.serieComprobanteService = serieComprobanteService;
    }

    @GetMapping("/api/listar")
    @PreAuthorize("hasAuthority('OPCION_8')")
    public ResponseEntity<?> listarVentasApi() {
        Map<String, Object> response = new HashMap<>();
        List<Venta> ventas = ventaService.listarVentas();
        response.put("success", true);
        response.put("data", ventas);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/ventas_id/{id}")
    @PreAuthorize("hasAuthority('OPCION_8')")
    public ResponseEntity<?> obtenerVentaPorId(@PathVariable Long id) {
        try {
            Venta venta = ventaService.obtenerVenta(id);
            if (venta == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Venta no encontrada"));
            }
            return ResponseEntity.ok(Map.of("success", true, "data", venta));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error al obtener la venta", "error", e.getMessage()));
        }
    }

    @GetMapping("/api/formaPago")
    @PreAuthorize("hasAuthority('OPCION_8')")
    public ResponseEntity<?> listarFormasPagoApi() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", formaPagoService.listarFormasPago());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/serieComprobante")
    @PreAuthorize("hasAuthority('OPCION_8')")
    public ResponseEntity<?> listarSeriesComprobanteApi() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", serieComprobanteService.listarSerieComprobante());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/guardar")
    @PreAuthorize("hasAuthority('OPCION_8')")
    public ResponseEntity<?> guardarVentaApi(@Valid @RequestBody VentaDTO venta) {
        try {
            Venta ventaGuardada = ventaService.crearVenta(venta);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Venta guardada exitosamente");
            response.put("data", ventaGuardada);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al guardar la venta: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/api/actualizar/{id}")
    @PreAuthorize("hasAuthority('OPCION_8')")
    public ResponseEntity<?> actualizarVenta(@PathVariable Long id, @Valid @RequestBody VentaDTO ventaRequest) {
        try {
            Venta ventaReemplazo = ventaService.reemplazarVenta(id, ventaRequest);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Venta actualizada correctamente");
            response.put("data", ventaReemplazo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al actualizar la venta: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/api/eliminar/{id}")
    @PreAuthorize("hasAuthority('OPCION_8')")
    public ResponseEntity<?> eliminarVentaApi(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Venta ventaEliminada = ventaService.anularVenta(id);
            response.put("success", true);
            response.put("message", "Venta anulada correctamente");
            response.put("data", ventaEliminada);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al anular venta: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/api/cuotas/{ventaId}")
    @PreAuthorize("hasAuthority('OPCION_8')")
    public ResponseEntity<?> listarCuotasPorVenta(@PathVariable Long ventaId) {
        try {
            List<Cuota> cuotas = ventaService.obtenerCuotasPorVenta(ventaId);
            return ResponseEntity.ok(cuotas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/api/pagos/{ventaId}")
    @PreAuthorize("hasAuthority('OPCION_8')")
    public ResponseEntity<?> listarPagosPorVenta(@PathVariable Long ventaId) {
        try {
            List<Pago> pagos = ventaService.obtenerPagosPorVenta(ventaId);
            return ResponseEntity.ok(pagos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/api/boleta/{ventaId}")
    @PreAuthorize("hasAuthority('OPCION_8')")
    public void descargarBoletaPDF(@PathVariable Long ventaId, HttpServletResponse response) {
        try {
            byte[] pdfBytes = generarBoletaService.generarBoletaPdf(ventaId);
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"boleta_" + ventaId + ".pdf\"");
            try (OutputStream outputStream = response.getOutputStream()) {
                outputStream.write(pdfBytes);
                outputStream.flush();
            }
        } catch (Exception e) {
            log.error("Error generando boleta PDF", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/api/envio-correo/{ventaid}")
    @PreAuthorize("hasAuthority('OPCION_8')")
    public ResponseEntity<?> enviarBoletaPorCorreo(@PathVariable Long ventaid) {
        Map<String, Object> response = new HashMap<>();
        try {
            String correoEnviado = generarBoletaService.enviarBoletaPorCorreo(ventaid);
            response.put("success", true);
            response.put("message", "Boleta enviada exitosamente a: " + correoEnviado);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error al enviar correo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
