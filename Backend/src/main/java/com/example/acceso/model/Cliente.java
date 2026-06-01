package com.example.acceso.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El documento es obligatorio")
    @Column(nullable = false)
    private String documento;

    @Pattern(regexp = "^$|\\d{9}", message = "El teléfono debe tener 9 dígitos si es ingresado")
    @Column(length = 9)
    private String telefono;

    @Email(message = "El correo debe tener un formato válido")
    @Column(unique = true)
    private String correo;

    @Column(nullable = false)
    private Integer estado = 1;
}
