package com.example.acceso.service.Implements;

import com.example.acceso.DTO.ReporteUtilidadProductoDTO;
import com.example.acceso.DTO.ReporteUtilidadVentaDTO;
import com.example.acceso.DTO.ReporteUttilidadUsuarioDTO;
import com.example.acceso.repository.ReportesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportesServiceImplTest {

    @Mock private ReportesRepository reportesRepository;
    @InjectMocks private ReportesServiceImpl reportesService;

    @Test
    void obtenerUtilidadPorVenta_deberiaDelegar() {
        when(reportesRepository.obtenerUtilidadPorVenta()).thenReturn(List.of(mock(ReporteUtilidadVentaDTO.class)));
        assertThat(reportesService.obtenerUtilidadPorVenta()).hasSize(1);
    }

    @Test
    void obtenerUtilidadPorVentaRango_fechasValidas_deberiaDelegar() {
        LocalDateTime inicio = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 1, 31, 23, 59);
        when(reportesRepository.obtenerUtilidadPorVentaRango(inicio, fin)).thenReturn(List.of());
        assertThat(reportesService.obtenerUtilidadPorVentaRango(inicio, fin)).isEmpty();
    }

    @Test
    void obtenerUtilidadPorVentaRango_finAntesDeInicio_deberiaLanzarExcepcion() {
        LocalDateTime inicio = LocalDateTime.of(2026, 2, 1, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 1, 1, 0, 0);
        assertThatThrownBy(() -> reportesService.obtenerUtilidadPorVentaRango(inicio, fin))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void obtenerUtilidadPorUsuario_deberiaDelegar() {
        when(reportesRepository.obtenerUtilidadPorUsuario()).thenReturn(List.of(mock(ReporteUttilidadUsuarioDTO.class)));
        assertThat(reportesService.obtenerUtilidadPorUsuario()).hasSize(1);
    }

    @Test
    void obtenerUtilidadPorUsuarioRango_fechasValidas_deberiaDelegar() {
        LocalDateTime inicio = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 1, 31, 23, 59);
        when(reportesRepository.obtenerUtilidadPorUsuarioRango(inicio, fin)).thenReturn(List.of());
        assertThat(reportesService.obtenerUtilidadPorUsuarioRango(inicio, fin)).isEmpty();
    }

    @Test
    void obtenerUtilidadPorUsuarioRango_finAntesDeInicio_deberiaLanzarExcepcion() {
        assertThatThrownBy(() -> reportesService.obtenerUtilidadPorUsuarioRango(
                LocalDateTime.of(2026, 2, 1, 0, 0),
                LocalDateTime.of(2026, 1, 1, 0, 0)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void obtenerUtilidadPorProducto_deberiaDelegar() {
        when(reportesRepository.obtenerUtilidadPorProducto()).thenReturn(List.of(mock(ReporteUtilidadProductoDTO.class)));
        assertThat(reportesService.obtenerUtilidadPorProducto()).hasSize(1);
    }

    @Test
    void obtenerUtilidadPorProductoRango_fechasValidas_deberiaDelegar() {
        LocalDateTime inicio = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 1, 31, 23, 59);
        when(reportesRepository.obtenerUtilidadPorProductoRango(inicio, fin)).thenReturn(List.of());
        assertThat(reportesService.obtenerUtilidadPorProductoRango(inicio, fin)).isEmpty();
    }

    @Test
    void obtenerUtilidadPorProductoRango_finAntesDeInicio_deberiaLanzarExcepcion() {
        assertThatThrownBy(() -> reportesService.obtenerUtilidadPorProductoRango(
                LocalDateTime.of(2026, 2, 1, 0, 0),
                LocalDateTime.of(2026, 1, 1, 0, 0)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
    }

    @Test
    void obtenerUtilidadPorVentaRango_ambosNulos_deberiaDelegar() {
        when(reportesRepository.obtenerUtilidadPorVentaRango(null, null)).thenReturn(List.of());
        assertThat(reportesService.obtenerUtilidadPorVentaRango(null, null)).isEmpty();
    }
}
