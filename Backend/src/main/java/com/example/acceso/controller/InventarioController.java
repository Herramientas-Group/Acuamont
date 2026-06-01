package com.example.acceso.controller;

import com.example.acceso.DTO.AjusteInventarioDTO;
import com.example.acceso.model.AjusteInventario;
import com.example.acceso.model.Producto;
import com.example.acceso.model.TipoMovimiento;
import com.example.acceso.model.Venta;
import com.example.acceso.service.Interfaces.AjusteInventarioService;
import com.example.acceso.service.Interfaces.InventarioService;
import com.example.acceso.service.Interfaces.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventario")
public class InventarioController {
    private final ProductoService productoService;
    private final InventarioService inventarioService;
    private final AjusteInventarioService ajusteInventarioService;
    public InventarioController(ProductoService productoService , InventarioService inventarioService, AjusteInventarioService ajusteInventarioService) {
        this.productoService = productoService;
        this.inventarioService = inventarioService;
        this.ajusteInventarioService = ajusteInventarioService;
    }

    @GetMapping("/api/listar")
    @PreAuthorize("hasAuthority('OPCION_9')")
    public ResponseEntity<?> listarProductosApi() {
        Map<String, Object> response = new HashMap<>();
        List<Producto> productos = productoService.listarProductos();
        response.put("success", true);
        response.put("data", productos);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/movimientos/{productoId}")
    @PreAuthorize("hasAuthority('OPCION_9')")
    public ResponseEntity<?> obtenerMovimientos(@PathVariable Long productoId) {
        List<Venta> movimientos = inventarioService.listarMovimientosPorProducto(productoId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", movimientos);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/ajustes/{productoId}")
    @PreAuthorize("hasAuthority('OPCION_9')")
    public ResponseEntity<?> obtenerAjustes(@PathVariable Long productoId) {
        List<AjusteInventario> ajustes = ajusteInventarioService.listarAjustePorProducto(productoId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", ajustes);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/guardarAjuste")
    @PreAuthorize("hasAuthority('OPCION_9')")
    public ResponseEntity<?> guardarAjuste(@RequestBody AjusteInventarioDTO ajusteInventarioDTO) {
        Map<String, Object> response = new HashMap<>();
        AjusteInventario nuevoAjuste = ajusteInventarioService.guardarAjuste(ajusteInventarioDTO);
        response.put("success", true);
        response.put("data", nuevoAjuste);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/tipoMovimientos")
    @PreAuthorize("hasAuthority('OPCION_9')")
    public ResponseEntity<?> obtenerTiposMovimientos() {
        List<TipoMovimiento> tiposMovimientos = inventarioService.listarTiposMovimientos();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", tiposMovimientos);
        return ResponseEntity.ok(response);
    }
}
