package com.example.acceso.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoProductoDTO {
    private LocalDateTime fecha;
    private String documento;
    private BigDecimal precioVenta;
    private Integer cantidad;
    private BigDecimal subtotal;
}
