package com.example.acceso.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LoginResponseDTO {
    private Long id;
    private String token;
    private String usuario;
    private String nombre;
    private String perfil;
    private List<OpcionDTO> opciones;
}
