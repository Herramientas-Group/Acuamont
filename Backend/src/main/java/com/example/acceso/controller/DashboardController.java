package com.example.acceso.controller;

import com.example.acceso.model.Venta;
import com.example.acceso.service.Interfaces.CategoriaService;
import com.example.acceso.service.Interfaces.ProductoService;
import com.example.acceso.service.Interfaces.UsuarioService;
import com.example.acceso.service.Interfaces.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final UsuarioService usuarioService;
    private final CategoriaService categoriaService;
    private final ProductoService productoService;
    private final VentaService ventaService;

    @GetMapping("/api/resumen")
    @PreAuthorize("hasAuthority('OPCION_1')")
    public ResponseEntity<?> obtenerResumen() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("totalUsuarios", usuarioService.contarUsuarios());
        data.put("totalCategorias", categoriaService.contarCategorias());
        data.put("totalProductos", productoService.contarProductos());
        data.put("totalVentasDia", ventaService.totalVentasDelDia());
        data.put("totalVentasMes", ventaService.totalVentasDelMes());
        data.put("totalDeuda", ventaService.totalDeuda());

        List<Map<String, Object>> cuentasPorCobrar = ventaService.listarCuentasPorCobrarPendientes()
                .stream()
                .map(v -> {
                    Map<String, Object> c = new LinkedHashMap<>();
                    c.put("idVenta", v.getId());
                    c.put("cliente", v.getCliente() != null ? v.getCliente().getNombre() : "");
                    c.put("fecha", v.getFecha() != null ? v.getFecha().toString() : "");
                    c.put("deuda", v.getDeuda());
                    return c;
                })
                .toList();
        data.put("cuentasPorCobrar", cuentasPorCobrar);

        List<Map<String, Object>> topProductos = productoService.findTop5ProductosMasVendidos()
                .stream()
                .map(row -> {
                    Map<String, Object> p = new LinkedHashMap<>();
                    p.put("producto", row[0]);
                    p.put("cantidadVendida", row[1]);
                    return p;
                })
                .toList();
        data.put("topProductos", topProductos);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("data", data);
        return ResponseEntity.ok(response);
    }
}
