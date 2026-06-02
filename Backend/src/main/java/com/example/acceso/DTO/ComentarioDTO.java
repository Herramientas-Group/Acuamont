package com.example.acceso.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComentarioDTO {
    private Long id;
    private String nombre;
    private String mensaje;
    private String imagen;
    private String fechaCreacion;
    private int estado = 1;
}
