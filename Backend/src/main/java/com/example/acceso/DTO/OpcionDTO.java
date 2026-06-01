package com.example.acceso.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class OpcionDTO {
    private Long id;
    private String nombre;
    private String ruta;
    private String icono;
}
