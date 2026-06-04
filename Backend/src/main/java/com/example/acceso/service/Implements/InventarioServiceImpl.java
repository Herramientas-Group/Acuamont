package com.example.acceso.service.Implements;

import com.example.acceso.DTO.MovimientoProductoDTO;
import com.example.acceso.model.TipoMovimiento;
import com.example.acceso.repository.TipoMovimientoRepository;
import com.example.acceso.repository.VentaRepository;
import com.example.acceso.service.Interfaces.InventarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventarioServiceImpl implements InventarioService {

    private final VentaRepository ventaRepository;
    private final TipoMovimientoRepository tipoMovimeintoRepository;

    @Transactional(readOnly = true)
    public List<MovimientoProductoDTO> listarMovimientosPorProducto(Long productoId) {
        return ventaRepository.findVentasByProductoId(productoId).stream()
                .flatMap(v -> v.getDetalleVentas().stream()
                        .map(d -> new MovimientoProductoDTO(
                                v.getFecha(),
                                v.getSerieComprobante().getSerie() + "-" + v.getCorrelativo(),
                                d.getPrecioUnitario(),
                                d.getCantidad(),
                                d.getSubtotal()
                        )))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TipoMovimiento> listarTiposMovimientos() {
        return tipoMovimeintoRepository.findAllByEstadoNot(2);
    }

}
