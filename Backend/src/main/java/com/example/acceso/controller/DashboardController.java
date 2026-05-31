package com.example.acceso.controller;

import com.example.acceso.model.Venta;
import com.example.acceso.service.Interfaces.CategoriaService;
import com.example.acceso.service.Interfaces.ProductoService;
import com.example.acceso.service.Interfaces.UsuarioService;
import com.example.acceso.service.Interfaces.VentaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DashboardController {
    private final UsuarioService usuarioService;
    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final VentaService ventaService;
    public DashboardController(UsuarioService usuarioService, ProductoService productoService, CategoriaService categoriaService, VentaService ventaService) {
        this.usuarioService = usuarioService;
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.ventaService = ventaService;
    }

    @GetMapping("/")
    public String mostrarDashboard(Model model) {
        long totalUsuarios = usuarioService.contarUsuarios();
        long totalProductos = productoService.contarProductos();
        long totalCategorias = categoriaService.contarCategorias();
        BigDecimal totalVentasDia = ventaService.totalVentasDelDia();
        BigDecimal totalVentasMes = ventaService.totalVentasDelMes();
        BigDecimal totalDeuda = ventaService.totalDeuda();
        List<Venta> cuentasPorCobrar = ventaService.listarCuentasPorCobrarPendientes();
        List<Object[]> topProductos = productoService.findTop5ProductosMasVendidos();
        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("totalCategorias",totalCategorias);
        model.addAttribute("totalProductos",totalProductos);
        model.addAttribute("totalVentasDia",totalVentasDia);
        model.addAttribute("totalVentasMes",totalVentasMes);
        model.addAttribute("totalDeuda",totalDeuda);
        model.addAttribute("cuentasPorCobrar", cuentasPorCobrar);
        model.addAttribute("topProductos", topProductos);
        return "index";
    }

    private final Path slidePath = Paths.get("slide-Inicio/");
    @GetMapping("/PrincipalPage-web")
    public String principalPage(Model model) throws IOException {
        if (!Files.exists(slidePath)) {
            Files.createDirectories(slidePath);
            model.addAttribute("slides", new ArrayList<>());
        } else {
            List<String> slides = Files.list(slidePath)
                    .filter(Files::isRegularFile)
                    .map(path -> "/slide-Inicio/" + path.getFileName().toString())
                    .collect(Collectors.toList());
            model.addAttribute("slides", slides);
        }
        return "PrincipalPage-WEB";
    }

    @GetMapping("/Contacto-web")
    public String mostrarPaginaContacto() {
        return "Contacto-WEB";
    }

    @GetMapping("/Productos-web")
    public String mostrarPaginaProductos() {
        return "Productos-WEB";
    }

    @GetMapping("/Servicios-web")
    public String mostrarPaginaServicios() {
        return "Servicios-WEB";
    }

    @GetMapping("/Comentarios-web")
    public String mostrarPaginaComentarios(){return "Comentarios-WEB";}
}
