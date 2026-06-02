package com.example.acceso.service.Interfaces;

import com.example.acceso.DTO.ComentarioDTO;

import java.util.List;

public interface ComentarioService {
    List<ComentarioDTO> listarComentarios();
    ComentarioDTO guardarComentario(String nombre, String mensaje, String imagenUrl);
}
