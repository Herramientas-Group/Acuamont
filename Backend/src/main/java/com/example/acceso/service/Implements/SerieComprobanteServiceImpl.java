package com.example.acceso.service.Implements;

import com.example.acceso.model.SerieComprobante;
import com.example.acceso.repository.SerieComprobanteRepository;
import com.example.acceso.service.Interfaces.SerieComprobanteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SerieComprobanteServiceImpl implements SerieComprobanteService {

    private final SerieComprobanteRepository serieComprobanteRepository;

    @Transactional(readOnly = true)
    public List<SerieComprobante> listarSerieComprobante() {
        return serieComprobanteRepository.findAllByEstadoNot(2);
    }

    @Transactional(readOnly = true)
    public Optional<SerieComprobante> obtenerSerieComprobantePorId(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return serieComprobanteRepository.findById(id);
    }

    @Transactional
    public Optional<SerieComprobante> eliminarLogicoSerieComprobante(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }

        return obtenerSerieComprobantePorId(id).map(serieComprobante -> {
            serieComprobante.setEstado(2);
            return serieComprobanteRepository.save(serieComprobante);
        });
    }

    @Transactional
    public Optional<SerieComprobante> cambiarEstadoSerieComprobante(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }

        return obtenerSerieComprobantePorId(id).map(serieComprobante -> {
            if (serieComprobante.getEstado() == 1) {
                serieComprobante.setEstado(0);
            } else if (serieComprobante.getEstado() == 0) {
                serieComprobante.setEstado(1);
            }
            return serieComprobanteRepository.save(serieComprobante);
        });
    }

}
