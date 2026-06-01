package com.example.acceso.model;

import jakarta.persistence.*;
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
@Table(name = "series_comprobante")
public class SerieComprobante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "La serie es obligatorio")
    @Size(min = 2, max = 100, message = "La serie debe tener entre 2 y 20 caracteres")
    @Column(nullable = false, length = 100)
    private String serie;

    @NotNull(message = "El correlativo actual no puede ser nulo")
    @Column(nullable = false)
    private Integer correlativo_actual;

    @Column(nullable = false)
    private Integer estado = 1;
}
