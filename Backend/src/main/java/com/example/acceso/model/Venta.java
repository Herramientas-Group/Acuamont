package com.example.acceso.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_SerieComprobante", nullable = false)
    private SerieComprobante serieComprobante;

    @NotNull
    @Column(nullable = false)
    private Integer correlativo;

    @ManyToOne
    @JoinColumn(name = "id_Cliente")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_Usuario")
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @ManyToOne
    @JoinColumn(name = "id_FormaPago")
    private FormaPago formaPago;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal deuda = BigDecimal.ZERO;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("venta-cuotas")
    private List<Cuota> cuotas = new ArrayList<>();

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("venta-detalles")
    private List<DetalleVenta> detalleVentas = new ArrayList<>();

    @Column(nullable = false)
    private Integer estado = 1;
}
