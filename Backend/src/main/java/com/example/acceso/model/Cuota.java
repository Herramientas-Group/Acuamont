package com.example.acceso.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cuotas")
public class Cuota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_venta", nullable = false)
    @JsonBackReference("venta-cuotas")
    private Venta venta;

    @Column(nullable = false)
    private Integer numeroCuota;

    @Column(nullable = false)
    private BigDecimal monto;

    @Column(nullable = false)
    private BigDecimal saldo;

    @Column(nullable = false)
    private LocalDate fechaVencimiento;

    @Column(nullable = false)
    private Integer estado = 0;
}
