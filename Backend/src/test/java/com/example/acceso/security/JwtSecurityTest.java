package com.example.acceso.security;

import com.example.acceso.config.JwtUtil;
import com.example.acceso.model.Perfil;
import com.example.acceso.model.Opcion;
import com.example.acceso.model.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class JwtSecurityTest {

    private static final String SECRET = "testSecretKeyForTestingPurposesOnly32Chars!";
    private static final long EXPIRATION = 28800000;

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET, EXPIRATION);
    }

    @Test
    void tokenConUsernameNormalEsValido() {
        String token = jwtUtil.generarToken("usuario.test");
        assertTrue(jwtUtil.validarToken(token));
        assertEquals("usuario.test", jwtUtil.extraerUsername(token));
    }

    @Test
    void tokenConUsernameLargoEsValido() {
        String longUsername = "u" + "a".repeat(200);
        String token = jwtUtil.generarToken(longUsername);
        assertTrue(jwtUtil.validarToken(token));
        assertTrue(jwtUtil.extraerUsername(token).length() > 200);
    }

    @Test
    void tokenConClaimsCompletosEsValido() {
        Usuario usuario = crearUsuario("admin", "Admin", "Administrador", List.of(1, 2, 3, 4, 5));
        String token = jwtUtil.generarToken(usuario);

        assertTrue(jwtUtil.validarToken(token));
        assertEquals("admin", jwtUtil.extraerUsername(token));
        assertEquals("Admin", jwtUtil.extraerNombre(token));
        assertEquals("Administrador", jwtUtil.extraerPerfil(token));
        assertThat(jwtUtil.extraerOpciones(token)).containsExactlyInAnyOrder(1, 2, 3, 4, 5);
    }

    @Test
    void tokenSinPerfilNoLanzaExcepcion() {
        Usuario usuario = crearUsuario("user", "User", null, List.of());
        String token = jwtUtil.generarToken(usuario);

        assertDoesNotThrow(() -> jwtUtil.validarToken(token));
        assertEquals("", jwtUtil.extraerPerfil(token));
    }

    @Test
    void tokenConOpcionesVaciasRetornaListaVacia() {
        Usuario usuario = crearUsuario("user", "User", "Perfil", List.of());
        String token = jwtUtil.generarToken(usuario);

        List<Integer> opciones = jwtUtil.extraerOpciones(token);
        assertNotNull(opciones);
        assertTrue(opciones.isEmpty());
    }

    @Test
    void tokenExpiradoEsRechazado() {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        String expiredToken = Jwts.builder()
            .subject("test")
            .issuedAt(new Date(System.currentTimeMillis() - 100000))
            .expiration(new Date(System.currentTimeMillis() - 50000))
            .signWith(key)
            .compact();

        assertFalse(jwtUtil.validarToken(expiredToken),
            "Token expirado debe ser rechazado");
    }

    @Test
    void tokenConFirmaInvalidaEsRechazado() {
        SecretKey wrongKey = Keys.hmacShaKeyFor(
            "unaLlaveDiferenteParaFirmaQueNoCoincide!".getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
            .subject("test")
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 3600000))
            .signWith(wrongKey)
            .compact();

        assertFalse(jwtUtil.validarToken(token),
            "Token con firma invalida debe ser rechazado");
    }

    @Test
    void tokenConClaimsMaliciososEsManejado() {
        String token = jwtUtil.generarToken("test");

        assertEquals("test", jwtUtil.extraerUsername(token));
        List<Integer> opciones = jwtUtil.extraerOpciones(token);
        assertNotNull(opciones);
        assertTrue(opciones.isEmpty());
    }

    @Test
    void extraerOpcionesConTokenNullRetornaListaVacia() {
        List<Integer> opciones = jwtUtil.extraerOpciones(null);
        assertNotNull(opciones);
        assertTrue(opciones.isEmpty());
    }

    @Test
    void tokenConPerfilNullNoAlteraOpciones() {
        Usuario usuario = new Usuario();
        usuario.setUsuario("test");
        usuario.setNombre("Test");
        usuario.setPerfil(null);

        String token = jwtUtil.generarToken(usuario);
        List<Integer> opciones = jwtUtil.extraerOpciones(token);

        assertNotNull(opciones);
        assertTrue(opciones.isEmpty());
    }

    @Test
    void tokenSobrescribeClaimsPreviamenteValidos() {
        Usuario usuario1 = crearUsuario("user1", "User One", "Admin", List.of(1, 2));
        Usuario usuario2 = crearUsuario("user2", "User Two", "User", List.of(3, 4));

        String token1 = jwtUtil.generarToken(usuario1);
        String token2 = jwtUtil.generarToken(usuario2);

        assertEquals("user1", jwtUtil.extraerUsername(token1));
        assertEquals("user2", jwtUtil.extraerUsername(token2));
    }

    private Usuario crearUsuario(String username, String nombre,
                                  String perfilNombre, List<Integer> opcionIds) {
        Usuario usuario = new Usuario();
        usuario.setUsuario(username);
        usuario.setNombre(nombre);
        if (perfilNombre != null) {
            Perfil perfil = new Perfil();
            perfil.setNombre(perfilNombre);
            Set<Opcion> opciones = opcionIds.stream().map(id -> {
                Opcion op = new Opcion();
                op.setId(id.longValue());
                return op;
            }).collect(java.util.stream.Collectors.toSet());
            perfil.setOpciones(opciones);
            usuario.setPerfil(perfil);
        }
        return usuario;
    }
}
