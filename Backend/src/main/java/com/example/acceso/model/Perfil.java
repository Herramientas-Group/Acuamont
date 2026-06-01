package com.example.acceso.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "perfiles")
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false)
    private int estado = 1;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "perfil_opcion", joinColumns = @JoinColumn(name = "id_perfil"), inverseJoinColumns = @JoinColumn(name = "id_opcion"))
    @JsonIgnoreProperties("perfiles")
    private Set<Opcion> opciones = new HashSet<>();
}
