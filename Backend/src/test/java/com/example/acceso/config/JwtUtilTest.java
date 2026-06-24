package com.example.acceso.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private static final String SECRET = "testSecretKeyForTestingPurposesOnly32Chars!";
    private static final long EXPIRATION = 28800000;

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET, EXPIRATION);
    }

    @Test
    void generarToken_deberiaContenerClaimsCorrectos() {
        var usuario = crearUsuario("testuser", "Test User", "Admin", List.of(1, 2, 3));
        String token = jwtUtil.generarToken(usuario);

        assertThat(jwtUtil.extraerUsername(token)).isEqualTo("testuser");
        assertThat(jwtUtil.extraerNombre(token)).isEqualTo("Test User");
        assertThat(jwtUtil.extraerPerfil(token)).isEqualTo("Admin");
        assertThat(jwtUtil.extraerOpciones(token)).containsExactlyInAnyOrder(1, 2, 3);
    }

    @Test
    void generarToken_conPerfilNull_noDebeLanzarExcepcion() {
        var usuario = crearUsuario("user2", "User Two", null, List.of());
        String token = jwtUtil.generarToken(usuario);

        assertThat(jwtUtil.extraerUsername(token)).isEqualTo("user2");
        assertThat(jwtUtil.extraerOpciones(token)).isEmpty();
    }

    @Test
    void generarToken_conOpcionesVacias_deberiaIncluirListaVacia() {
        var usuario = crearUsuario("user3", "User Three", "Perfil", List.of());
        String token = jwtUtil.generarToken(usuario);

        assertThat(jwtUtil.extraerOpciones(token)).isEmpty();
    }

    @Test
    void extraerOpciones_conListaDeLong_deberiaConvertirAInteger() {
        var usuario = crearUsuario("user4", "User Four", "Perfil", List.of(5, 6, 7));
        String token = jwtUtil.generarToken(usuario);

        List<Integer> opciones = jwtUtil.extraerOpciones(token);
        assertThat(opciones).containsExactlyInAnyOrder(5, 6, 7);
        assertThat(opciones).allMatch(e -> e instanceof Integer);
    }

    @Test
    void extraerOpciones_tokenSinClaimOpciones_deberiaRetornarListaVacia() {
        String token = jwtUtil.generarToken("simpleuser");
        assertThat(jwtUtil.extraerOpciones(token)).isEmpty();
    }

    @Test
    void extraerUsername_deberiaRetornarSubject() {
        String token = jwtUtil.generarToken("miUsuario");
        assertThat(jwtUtil.extraerUsername(token)).isEqualTo("miUsuario");
    }

    @Test
    void extraerNombre_deberiaRetornarNombre() {
        var usuario = crearUsuario("test", "Nombre Test", null, List.of());
        String token = jwtUtil.generarToken(usuario);
        assertThat(jwtUtil.extraerNombre(token)).isEqualTo("Nombre Test");
    }

    @Test
    void extraerPerfil_deberiaRetornarPerfil() {
        var usuario = crearUsuario("test", "Name", "Administrador", List.of());
        String token = jwtUtil.generarToken(usuario);
        assertThat(jwtUtil.extraerPerfil(token)).isEqualTo("Administrador");
    }

    @Test
    void validarToken_tokenValido_deberiaRetornarTrue() {
        String token = jwtUtil.generarToken("user");
        assertThat(jwtUtil.validarToken(token)).isTrue();
    }

    @Test
    void validarToken_tokenMalformado_deberiaRetornarFalse() {
        assertThat(jwtUtil.validarToken("token-invalido")).isFalse();
    }

    @Test
    void validarToken_tokenVacio_deberiaRetornarFalse() {
        assertThat(jwtUtil.validarToken("")).isFalse();
    }

    private com.example.acceso.model.Usuario crearUsuario(
            String username, String nombre, String perfilNombre, List<Integer> opcionIds) {
        var usuario = new com.example.acceso.model.Usuario();
        usuario.setUsuario(username);
        usuario.setNombre(nombre);
        if (perfilNombre != null) {
            var perfil = new com.example.acceso.model.Perfil();
            perfil.setNombre(perfilNombre);
            var opciones = opcionIds.stream().map(id -> {
                var op = new com.example.acceso.model.Opcion();
                op.setId(id.longValue());
                return op;
            }).collect(java.util.stream.Collectors.toSet());
            perfil.setOpciones(opciones);
            usuario.setPerfil(perfil);
        }
        return usuario;
    }
}
