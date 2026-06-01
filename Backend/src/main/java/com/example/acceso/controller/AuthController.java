package com.example.acceso.controller;

import com.example.acceso.DTO.LoginRequestDTO;
import com.example.acceso.DTO.LoginResponseDTO;
import com.example.acceso.DTO.OpcionDTO;
import com.example.acceso.config.JwtUtil;
import com.example.acceso.model.Usuario;
import com.example.acceso.service.Interfaces.ServicioAutenticacionDosPasos;
import com.example.acceso.service.Interfaces.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;
    private final ServicioAutenticacionDosPasos servicio2FA;

    public AuthController(UsuarioService usuarioService, JwtUtil jwtUtil,
                          ServicioAutenticacionDosPasos servicio2FA) {
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
        this.servicio2FA = servicio2FA;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request) {
        Optional<Usuario> usuarioOpt = usuarioService.findByUsuario(request.getUsuario());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Credenciales inválidas"));
        }

        Usuario usuario = usuarioOpt.get();
        if (usuario.getEstado() != 1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Usuario inactivo"));
        }

        if (!usuarioService.verificarContrasena(request.getClave(), usuario.getClave())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Credenciales inválidas"));
        }

        if (usuario.isUsa2FA()) {
            if (request.getToken() == null || request.getToken().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Se requiere el token de seguridad"));
            }
            if (!servicio2FA.esCodigoValido(usuario.getSecreto2FA(), request.getToken())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Token de seguridad incorrecto"));
            }
        }

        String token = jwtUtil.generarToken(usuario);

        String nombrePerfil = usuario.getPerfil() != null ? usuario.getPerfil().getNombre() : "";
        List<OpcionDTO> opciones = usuario.getPerfil() != null
                ? usuario.getPerfil().getOpciones().stream()
                        .sorted(Comparator.comparing(op -> op.getId()))
                        .map(op -> new OpcionDTO(op.getId(), op.getNombre(), op.getRuta(), op.getIcono()))
                        .toList()
                : List.of();

        LoginResponseDTO response = LoginResponseDTO.builder()
                .token(token)
                .usuario(usuario.getUsuario())
                .nombre(usuario.getNombre())
                .perfil(nombrePerfil)
                .opciones(opciones)
                .build();

        return ResponseEntity.ok(response);
    }

    private record ErrorResponse(String message) {}
}
