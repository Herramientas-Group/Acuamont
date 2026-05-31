package com.example.acceso.service.Implements;

import com.example.acceso.model.FormaPago;
import com.example.acceso.repository.FormaPagoRepository;
import com.example.acceso.service.Interfaces.FormaPagoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FormaPagoServiceImpl implements FormaPagoService {

    private final FormaPagoRepository formaPagoRepository;

    @Transactional(readOnly = true)
    public List<FormaPago> listarFormasPago() {
        return formaPagoRepository.findAllByEstadoNot(2);
    }

    @Transactional(readOnly = true)
    public Optional<FormaPago> obtenerFormaPagoPorId(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }

        return formaPagoRepository.findById(id);
    }

}
