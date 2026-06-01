package com.example.acceso.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaDTO {

    @NotNull(message = "El ID del cliente es obligatorio.")
    private Long clienteId;

    @NotNull(message = "El ID del usuario (vendedor) es obligatorio.")
    private Long usuarioId;

    @NotNull(message = "El ID de la serie del comprobante es obligatorio.")
    private Long serieComprobanteId;

    @NotNull(message = "El ID de la forma de pago es obligatorio.")
    private Long formaPagoId;

    @NotEmpty(message = "La venta debe contener al menos un producto.")
    @Valid
    private List<DetalleVentaDTO> detalles;

    private BigDecimal montoInicial;

    private List<CuotasProgramadasDTO> planDeCuotas;
}
