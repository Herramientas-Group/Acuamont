package com.example.acceso.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AjusteInventarioDTO {

    private Long productoId;
    private Long tipoMovimientoId;
    private Integer cantidad;
    private String comentario;
}
