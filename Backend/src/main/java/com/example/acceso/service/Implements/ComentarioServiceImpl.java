package com.example.acceso.service.Implements;

import com.example.acceso.DTO.ComentarioDTO;
import com.example.acceso.model.Comentario;
import com.example.acceso.repository.ComentarioRepository;
import com.example.acceso.service.Interfaces.ComentarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComentarioServiceImpl implements ComentarioService {

    private final ComentarioRepository comentarioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ComentarioDTO> listarComentarios() {
        return comentarioRepository.findAllByOrderByFechaDesc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ComentarioDTO guardarComentario(String nombre, String mensaje, String imagenUrl) {
        Comentario comentario = new Comentario();
        comentario.setNombre(nombre);
        comentario.setComentario(mensaje);
        comentario.setImagenUrl(imagenUrl);
        comentario = comentarioRepository.save(comentario);
        return toDTO(comentario);
    }

    private ComentarioDTO toDTO(Comentario c) {
        ComentarioDTO dto = new ComentarioDTO();
        dto.setId(c.getId());
        dto.setNombre(c.getNombre());
        dto.setMensaje(c.getComentario());
        dto.setImagen(c.getImagenUrl());
        dto.setFechaCreacion(c.getFecha() != null
                ? c.getFecha().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : null);
        dto.setEstado(1);
        return dto;
    }
}
