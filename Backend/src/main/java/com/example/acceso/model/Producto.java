package com.example.acceso.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 2, max = 255, message = "La descripción debe tener entre 2 y 255 caracteres")
    @Column(nullable = false, length = 255)
    private String descripcion;

    @NotNull(message = "El precio de compra es obligatorio")
    @Min(value = 0, message = "El precio de compra debe ser mayor o igual a 0")
    @Column(nullable = false)
    private Double precioCompra;

    @NotNull(message = "El precio de venta es obligatorio")
    @Min(value = 0, message = "El precio de venta debe ser mayor o igual a 0")
    @Column(nullable = false)
    private Double precioVenta;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
    @Column(nullable = false)
    private Integer stock;

    @NotNull(message = "El stock de seguridad es obligatorio")
    @Min(value = 0, message = "El stock de seguridad debe ser mayor o igual a 0")
    @Column(nullable = false)
    private Integer stockSeguridad;

    @Column(columnDefinition = "TEXT")
    private String imagen;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    @Column(nullable = false)
    private Integer estado = 1;
}
