package com.example.acceso.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagosDTO {

    @NotNull
    private Long cuotaId;

    @NotNull
    @Positive
    private BigDecimal montoPagado;

    @Size(max = 250, message = "El comentario debe tener máximo 250 caracteres")
    private String comentario;

    @NotBlank(message = "Debe seleccionar un método de pago.")
    private String metodoPago;
}
