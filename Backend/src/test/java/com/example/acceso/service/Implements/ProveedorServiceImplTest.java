package com.example.acceso.service.Implements;

import com.example.acceso.model.Proveedor;
import com.example.acceso.repository.ProveedorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProveedorServiceImplTest {

    @Mock private ProveedorRepository proveedorRepository;
    @InjectMocks private ProveedorServiceImpl proveedorService;

    private Proveedor proveedor;

    @BeforeEach
    void setUp() {
        proveedor = new Proveedor();
        proveedor.setId(1L);
        proveedor.setNombre("Proveedor Test");
        proveedor.setDocumento("20123456789");
        proveedor.setEstado(1);
    }

    @Test
    void listarProveedores_deberiaRetornarSoloActivos() {
        when(proveedorRepository.findAllByEstadoNot(2)).thenReturn(List.of(proveedor));
        assertThat(proveedorService.listarProveedores()).hasSize(1);
    }

    @Test
    void guardarProveedor_nuevo_deberiaGuardar() {
        proveedor.setId(null);
        when(proveedorRepository.findByDocumento("20123456789")).thenReturn(Optional.empty());
        when(proveedorRepository.save(any(Proveedor.class))).thenReturn(proveedor);

        Proveedor result = proveedorService.guardarProveedor(proveedor);

        assertThat(result.getNombre()).isEqualTo("Proveedor Test");
        verify(proveedorRepository).save(proveedor);
    }

    @Test
    void guardarProveedor_existente_deberiaActualizar() {
        when(proveedorRepository.findByDocumento("20123456789")).thenReturn(Optional.of(proveedor));
        when(proveedorRepository.save(any(Proveedor.class))).thenReturn(proveedor);

        Proveedor result = proveedorService.guardarProveedor(proveedor);

        assertThat(result.getNombre()).isEqualTo("Proveedor Test");
    }

    @Test
    void guardarProveedor_documentoDuplicado_deberiaLanzarExcepcion() {
        Proveedor otro = new Proveedor();
        otro.setId(2L);
        otro.setDocumento("20123456789");
        when(proveedorRepository.findByDocumento("20123456789")).thenReturn(Optional.of(otro));

        assertThatThrownBy(() -> proveedorService.guardarProveedor(proveedor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("documento");
        verify(proveedorRepository, never()).save(any());
    }

    @Test
    void guardarProveedor_nombreVacio_deberiaLanzarExcepcion() {
        proveedor.setNombre("");
        assertThatThrownBy(() -> proveedorService.guardarProveedor(proveedor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nombre");
    }

    @Test
    void guardarProveedor_documentoVacio_deberiaLanzarExcepcion() {
        proveedor.setDocumento("");
        assertThatThrownBy(() -> proveedorService.guardarProveedor(proveedor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("documento");
    }

    @Test
    void guardarProveedor_dataIntegrity_deberiaCapturar() {
        proveedor.setId(null);
        when(proveedorRepository.findByDocumento("20123456789")).thenReturn(Optional.empty());
        when(proveedorRepository.save(any(Proveedor.class)))
                .thenThrow(new DataIntegrityViolationException("documento duplicado"));

        assertThatThrownBy(() -> proveedorService.guardarProveedor(proveedor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("documento");
    }

    @Test
    void obtenerProveedorPorId_valido_deberiaRetornar() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor));
        assertThat(proveedorService.obtenerProveedorPorId(1L)).isPresent();
    }

    @Test
    void obtenerProveedorPorId_invalido_deberiaRetornarEmpty() {
        assertThat(proveedorService.obtenerProveedorPorId(-1L)).isEmpty();
    }

    @Test
    void obtenerProveedorPorDocumento_deberiaDelegar() {
        when(proveedorRepository.findByDocumento("20123456789")).thenReturn(Optional.of(proveedor));
        assertThat(proveedorService.obtenerProveedorPorDocumento("20123456789")).isPresent();
    }

    @Test
    void eliminarProveedor_activo_deberiaHacerSoftDelete() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor));
        when(proveedorRepository.save(any(Proveedor.class))).thenReturn(proveedor);

        proveedorService.eliminarProveedor(1L);
        assertThat(proveedor.getEstado()).isEqualTo(2);
    }

    @Test
    void eliminarProveedor_idNulo_deberiaLanzarExcepcion() {
        assertThatThrownBy(() -> proveedorService.eliminarProveedor(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void eliminarProveedor_noEncontrado_deberiaLanzarExcepcion() {
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> proveedorService.eliminarProveedor(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no encontrado");
    }

    @Test
    void cambiarEstadoProveedor_activo_deberiaCambiarAInactivo() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor));
        when(proveedorRepository.save(any(Proveedor.class))).thenReturn(proveedor);

        Optional<Proveedor> result = proveedorService.cambiarEstadoProveedor(1L);
        assertThat(result).isPresent();
        assertThat(result.get().getEstado()).isEqualTo(0);
    }

    @Test
    void cambiarEstadoProveedor_idNulo_deberiaLanzarExcepcion() {
        assertThatThrownBy(() -> proveedorService.cambiarEstadoProveedor(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void contarProveedores_deberiaDelegar() {
        when(proveedorRepository.countByEstadoNot(2)).thenReturn(10L);
        assertThat(proveedorService.contarProveedores()).isEqualTo(10L);
    }
}
