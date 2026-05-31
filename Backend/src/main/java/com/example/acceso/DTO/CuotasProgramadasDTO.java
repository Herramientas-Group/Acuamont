package com.example.acceso.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuotasProgramadasDTO {

    @NotNull(message = "El monto de la cuota no puede ser nulo.")
    private BigDecimal monto;

    @NotNull(message = "La fecha de vencimiento de la cuota no puede ser nula.")
    private LocalDate fechaVencimiento;
}
