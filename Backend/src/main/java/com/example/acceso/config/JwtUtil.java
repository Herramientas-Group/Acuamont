package com.example.acceso.config;

import com.example.acceso.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expirationTime;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationTime = expiration;
    }

    public String generarToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();
    }

    public String generarToken(Usuario usuario) {
        String perfil = usuario.getPerfil() != null ? usuario.getPerfil().getNombre() : "";
        List<Integer> opciones = usuario.getPerfil() != null
                ? usuario.getPerfil().getOpciones().stream().map(op -> op.getId().intValue()).toList()
                : List.of();
        return Jwts.builder()
                .subject(usuario.getUsuario())
                .claim("nombre", usuario.getNombre())
                .claim("perfil", perfil)
                .claim("opciones", opciones)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();
    }

    public String extraerUsername(String token) {
        return getClaims(token).getSubject();
    }

    public String extraerNombre(String token) {
        return getClaims(token).get("nombre", String.class);
    }

    public String extraerPerfil(String token) {
        return getClaims(token).get("perfil", String.class);
    }

    public List<Integer> extraerOpciones(String token) {
        try {
            Object raw = getClaims(token).get("opciones");
            log.debug("Tipo de claim 'opciones': {}", raw != null ? raw.getClass().getName() : "null");
            if (raw instanceof List<?> list) {
                List<Integer> result = list.stream()
                        .map(e -> {
                            if (e instanceof Number n) return n.intValue();
                            log.warn("Elemento no-Numeric en opciones: {} ({})", e, e != null ? e.getClass().getName() : "null");
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .toList();
                log.debug("Opciones extraídas: {}", result);
                return result;
            }
            log.warn("Claim 'opciones' no es una Lista, tipo: {}", raw != null ? raw.getClass().getName() : "null");
            return List.of();
        } catch (Exception e) {
            log.error("Error extrayendo opciones del token", e);
            return List.of();
        }
    }

    public boolean validarToken(String token) {
        try {
            return getClaims(token).getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}