package com.example.acceso.service.Implements;

import com.example.acceso.DTO.ReporteUtilidadProductoDTO;
import com.example.acceso.DTO.ReporteUtilidadVentaDTO;
import com.example.acceso.DTO.ReporteUttilidadUsuarioDTO;
import com.example.acceso.repository.ReportesRepository;
import com.example.acceso.service.Interfaces.ReportesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportesServiceImpl implements ReportesService {

    private final ReportesRepository reportesRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ReporteUtilidadVentaDTO> obtenerUtilidadPorVenta() {
        return reportesRepository.obtenerUtilidadPorVenta();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteUtilidadVentaDTO> obtenerUtilidadPorVentaRango(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio != null && fin != null && fin.isBefore(inicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio");
        }
        return reportesRepository.obtenerUtilidadPorVentaRango(inicio, fin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteUttilidadUsuarioDTO> obtenerUtilidadPorUsuario() {
        return reportesRepository.obtenerUtilidadPorUsuario();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteUttilidadUsuarioDTO> obtenerUtilidadPorUsuarioRango(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio != null && fin != null && fin.isBefore(inicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio");
        }
        return reportesRepository.obtenerUtilidadPorUsuarioRango(inicio,fin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteUtilidadProductoDTO> obtenerUtilidadPorProducto() {
        return reportesRepository.obtenerUtilidadPorProducto();    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteUtilidadProductoDTO> obtenerUtilidadPorProductoRango(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio != null && fin != null && fin.isBefore(inicio)) {
            throw new IllegalArgumentException("Rango de fechas inválido");
        }
        return reportesRepository.obtenerUtilidadPorProductoRango(inicio, fin);
    }
}
