package com.example.acceso.service.Interfaces;

import com.example.acceso.DTO.MovimientoProductoDTO;
import com.example.acceso.model.TipoMovimiento;

import java.util.List;

public interface InventarioService {

    List<MovimientoProductoDTO> listarMovimientosPorProducto(Long productoId);

    List<TipoMovimiento> listarTiposMovimientos();

}
