package com.example.acceso.service.Implements;

import com.example.acceso.DTO.AjusteInventarioDTO;
import com.example.acceso.model.AjusteInventario;
import com.example.acceso.model.Producto;
import com.example.acceso.model.TipoMovimiento;
import com.example.acceso.repository.AjusteInventarioRepository;
import com.example.acceso.repository.TipoMovimientoRepository;
import com.example.acceso.service.Interfaces.AjusteInventarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AjusteInventarioServiceImpl implements AjusteInventarioService {

    private final AjusteInventarioRepository ajusteInventarioRepository;
    private final TipoMovimientoRepository tipoMovimientoRepository;
    private final ProductoServiceImpl productoService;

    @Transactional
    public AjusteInventario guardarAjuste(AjusteInventarioDTO ajusteInventarioDTO) {

        Producto producto = productoService.obtenerProductoPorId(ajusteInventarioDTO.getProductoId())
                .orElseThrow(() -> new RuntimeException(
                        "Error: Producto no encontrado con ID: " + ajusteInventarioDTO.getProductoId()));

        TipoMovimiento tipo = tipoMovimientoRepository.findById(ajusteInventarioDTO.getTipoMovimientoId())
                .orElseThrow(() -> new RuntimeException("Error: Tipo de Movimiento no encontrado con ID: "
                        + ajusteInventarioDTO.getTipoMovimientoId()));

        AjusteInventario nuevoAjuste = new AjusteInventario(producto, tipo, ajusteInventarioDTO.getCantidad(),
                ajusteInventarioDTO.getComentario());

        switch (tipo.getNombre()) {
            case "Entrada":
                nuevoAjuste.getProducto().setStock(nuevoAjuste.getProducto().getStock() + nuevoAjuste.getCantidad());
                break;
            case "Salida":
                if (producto.getStock() < nuevoAjuste.getCantidad()) {
                    throw new RuntimeException("Error: Stock insuficiente. Stock actual: " + producto.getStock());
                } else {
                    nuevoAjuste.getProducto()
                            .setStock(nuevoAjuste.getProducto().getStock() - nuevoAjuste.getCantidad());
                }
                break;
            default:
                break;
        }

        ajusteInventarioRepository.save(nuevoAjuste);
        return nuevoAjuste;

    }

    @Transactional(readOnly = true)
    public List<AjusteInventario> listarAjustePorProducto(Long id) {
        return ajusteInventarioRepository.findByProductoId(id);
    }

}
