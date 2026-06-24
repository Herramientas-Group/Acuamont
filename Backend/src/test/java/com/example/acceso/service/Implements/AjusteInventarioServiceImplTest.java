package com.example.acceso.service.Implements;

import com.example.acceso.DTO.AjusteInventarioDTO;
import com.example.acceso.model.AjusteInventario;
import com.example.acceso.model.Producto;
import com.example.acceso.model.TipoMovimiento;
import com.example.acceso.repository.AjusteInventarioRepository;
import com.example.acceso.repository.TipoMovimientoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AjusteInventarioServiceImplTest {

    @Mock private AjusteInventarioRepository ajusteInventarioRepository;
    @Mock private TipoMovimientoRepository tipoMovimientoRepository;
    @Mock private ProductoServiceImpl productoService;
    @InjectMocks private AjusteInventarioServiceImpl ajusteService;

    private Producto producto;
    private TipoMovimiento entrada;
    private TipoMovimiento salida;
    private AjusteInventarioDTO dto;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Producto Test");
        producto.setStock(20);

        entrada = new TipoMovimiento();
        entrada.setId(1L);
        entrada.setNombre("Entrada");

        salida = new TipoMovimiento();
        salida.setId(2L);
        salida.setNombre("Salida");

        dto = new AjusteInventarioDTO();
        dto.setProductoId(1L);
        dto.setTipoMovimientoId(1L);
        dto.setCantidad(5);
        dto.setComentario("Ajuste de prueba");
    }

    @Test
    void guardarAjuste_entrada_deberiaIncrementarStock() {
        when(productoService.obtenerProductoPorId(1L)).thenReturn(Optional.of(producto));
        when(tipoMovimientoRepository.findById(1L)).thenReturn(Optional.of(entrada));
        when(ajusteInventarioRepository.save(any(AjusteInventario.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        AjusteInventario result = ajusteService.guardarAjuste(dto);

        assertThat(result.getProducto().getStock()).isEqualTo(25);
        assertThat(result.getCantidad()).isEqualTo(5);
        assertThat(result.getComentario()).isEqualTo("Ajuste de prueba");
        verify(ajusteInventarioRepository).save(any(AjusteInventario.class));
    }

    @Test
    void guardarAjuste_salida_deberiaDecrementarStock() {
        dto.setTipoMovimientoId(2L);
        when(productoService.obtenerProductoPorId(1L)).thenReturn(Optional.of(producto));
        when(tipoMovimientoRepository.findById(2L)).thenReturn(Optional.of(salida));
        when(ajusteInventarioRepository.save(any(AjusteInventario.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        AjusteInventario result = ajusteService.guardarAjuste(dto);

        assertThat(result.getProducto().getStock()).isEqualTo(15);
    }

    @Test
    void guardarAjuste_salidaStockInsuficiente_deberiaLanzarExcepcion() {
        dto.setTipoMovimientoId(2L);
        dto.setCantidad(99);
        when(productoService.obtenerProductoPorId(1L)).thenReturn(Optional.of(producto));
        when(tipoMovimientoRepository.findById(2L)).thenReturn(Optional.of(salida));

        assertThatThrownBy(() -> ajusteService.guardarAjuste(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Stock insuficiente");
    }

    @Test
    void guardarAjuste_productoNoEncontrado_deberiaLanzarExcepcion() {
        when(productoService.obtenerProductoPorId(99L)).thenReturn(Optional.empty());
        dto.setProductoId(99L);

        assertThatThrownBy(() -> ajusteService.guardarAjuste(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Producto no encontrado");
    }

    @Test
    void guardarAjuste_tipoMovimientoNoEncontrado_deberiaLanzarExcepcion() {
        when(productoService.obtenerProductoPorId(1L)).thenReturn(Optional.of(producto));
        when(tipoMovimientoRepository.findById(99L)).thenReturn(Optional.empty());
        dto.setTipoMovimientoId(99L);

        assertThatThrownBy(() -> ajusteService.guardarAjuste(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Tipo de Movimiento no encontrado");
    }

    @Test
    void listarAjustePorProducto_deberiaDelegar() {
        when(ajusteInventarioRepository.findByProductoId(1L)).thenReturn(List.of(new AjusteInventario()));
        assertThat(ajusteService.listarAjustePorProducto(1L)).hasSize(1);
    }
}
