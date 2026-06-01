package com.example.acceso.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class LoginRequestDTO {
    @NotBlank
    private String usuario;
    @NotBlank
    private String clave;
    private String token;
}
