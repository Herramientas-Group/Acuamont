package com.example.acceso.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVentaDTO {

    @NotNull(message = "El ID del producto es obligatorio.")
    private Long productoId;

    @Min(value = 1, message = "La cantidad del producto debe ser como mínimo 1.")
    private int cantidad;
}
