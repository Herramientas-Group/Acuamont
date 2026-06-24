package com.example.acceso.service.Implements;

import com.example.acceso.model.Usuario;
import com.example.acceso.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock private UsuarioRepository usuarioRepository;
    @InjectMocks private UsuarioServiceImpl usuarioService;

    private Usuario usuario;
    private Usuario usuarioExistente;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Admin Test");
        usuario.setUsuario("admin");
        usuario.setCorreo("admin@test.com");
        usuario.setClave("password123");
        usuario.setEstado(1);

        usuarioExistente = new Usuario();
        usuarioExistente.setId(1L);
        usuarioExistente.setNombre("Admin Test");
        usuarioExistente.setUsuario("admin");
        usuarioExistente.setCorreo("admin@test.com");
        usuarioExistente.setClave("$2a$10$hashExistenteHash");
        usuarioExistente.setEstado(1);
    }

    // ========== listarUsuarios ==========

    @Test
    void listarUsuarios_deberiaRetornarSoloActivos() {
        when(usuarioRepository.findAllByEstadoNot(2)).thenReturn(List.of(usuario));
        assertThat(usuarioService.listarUsuarios()).hasSize(1);
    }

    // ========== guardarUsuario - Nuevo ==========

    @Test
    void guardarUsuario_nuevo_deberiaEncriptarClaveYGuardar() {
        usuario.setId(null);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario result = usuarioService.guardarUsuario(usuario);

        assertThat(result.getClave()).startsWith("$2a$");
        assertThat(result.getEstado()).isEqualTo(1);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void guardarUsuario_nuevo_sinClave_deberiaLanzarExcepcion() {
        usuario.setId(null);
        usuario.setClave("");
        assertThatThrownBy(() -> usuarioService.guardarUsuario(usuario))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("contraseña");
    }

    @Test
    void guardarUsuario_nombreVacio_deberiaLanzarExcepcion() {
        usuario.setNombre("");
        assertThatThrownBy(() -> usuarioService.guardarUsuario(usuario))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("nombre");
    }

    @Test
    void guardarUsuario_usuarioVacio_deberiaLanzarExcepcion() {
        usuario.setUsuario("");
        assertThatThrownBy(() -> usuarioService.guardarUsuario(usuario))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("usuario");
    }

    @Test
    void guardarUsuario_correoVacio_deberiaLanzarExcepcion() {
        usuario.setCorreo("");
        assertThatThrownBy(() -> usuarioService.guardarUsuario(usuario))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("correo");
    }

    // ========== guardarUsuario - Actualizar ==========

    @Test
    void guardarUsuario_existente_conNuevaClave_deberiaReencriptar() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        usuario.setClave("nuevaClave123");
        Usuario result = usuarioService.guardarUsuario(usuario);

        assertThat(result.getClave()).startsWith("$2a$");
        assertThat(result.getClave()).isNotEqualTo("nuevaClave123");
    }

    @Test
    void guardarUsuario_existente_sinClave_deberiaMantenerAnterior() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        usuario.setClave("");
        Usuario result = usuarioService.guardarUsuario(usuario);

        assertThat(result.getClave()).isEqualTo("$2a$10$hashExistenteHash");
    }

    @Test
    void guardarUsuario_existente_noEncontrado_deberiaLanzarExcepcion() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.guardarUsuario(usuario))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("actualizar");
    }

    // ========== DataIntegrity ==========

    @Test
    void guardarUsuario_usuarioDuplicado_deberiaCapturarDataIntegrity() {
        usuario.setId(null);
        when(usuarioRepository.save(any(Usuario.class)))
                .thenThrow(new DataIntegrityViolationException("llave duplicada usuario"));

        assertThatThrownBy(() -> usuarioService.guardarUsuario(usuario))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("usuario ya existe");
    }

    @Test
    void guardarUsuario_correoDuplicado_deberiaCapturarDataIntegrity() {
        usuario.setId(null);
        when(usuarioRepository.save(any(Usuario.class)))
                .thenThrow(new DataIntegrityViolationException("correo duplicado"));

        assertThatThrownBy(() -> usuarioService.guardarUsuario(usuario))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("correo");
    }

    // ========== verificarContrasena ==========

    @Test
    void verificarContrasena_correcta_deberiaRetornarTrue() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("password123");
        assertThat(usuarioService.verificarContrasena("password123", hash)).isTrue();
    }

    @Test
    void verificarContrasena_incorrecta_deberiaRetornarFalse() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("password123");
        assertThat(usuarioService.verificarContrasena("wrong", hash)).isFalse();
    }

    // ========== activar2FA ==========

    @Test
    void activar2FA_deberiaGuardarSecretoYActivarFlag() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        usuarioService.activar2FA(1L, "SECRETO123");

        assertThat(usuario.getSecreto2FA()).isEqualTo("SECRETO123");
        assertThat(usuario.isUsa2FA()).isTrue();
    }

    @Test
    void activar2FA_usuarioNoEncontrado_deberiaLanzarExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> usuarioService.activar2FA(99L, "secreto"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ========== eliminarUsuario ==========

    @Test
    void eliminarUsuario_activo_deberiaHacerSoftDelete() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        usuarioService.eliminarUsuario(1L);
        assertThat(usuario.getEstado()).isEqualTo(2);
    }

    @Test
    void eliminarUsuario_idNulo_deberiaLanzarExcepcion() {
        assertThatThrownBy(() -> usuarioService.eliminarUsuario(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ========== cambiarEstadoUsuario ==========

    @Test
    void cambiarEstadoUsuario_deberiaTogglear() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Optional<Usuario> result = usuarioService.cambiarEstadoUsuario(1L);
        assertThat(result).isPresent();
        assertThat(result.get().getEstado()).isEqualTo(0);
    }

    @Test
    void cambiarEstadoUsuario_idNulo_deberiaRetornarEmpty() {
        assertThat(usuarioService.cambiarEstadoUsuario(null)).isEmpty();
    }

    // ========== existeUsuario / existeCorreo ==========

    @Test
    void existeUsuario_existente_deberiaRetornarTrue() {
        when(usuarioRepository.existsByUsuario("admin")).thenReturn(true);
        assertThat(usuarioService.existeUsuario("admin")).isTrue();
    }

    @Test
    void existeUsuario_nulo_deberiaRetornarFalse() {
        assertThat(usuarioService.existeUsuario(null)).isFalse();
    }

    @Test
    void existeCorreo_existente_deberiaRetornarTrue() {
        when(usuarioRepository.existsByCorreo("admin@test.com")).thenReturn(true);
        assertThat(usuarioService.existeCorreo("admin@test.com")).isTrue();
    }

    // ========== findByUsuario ==========

    @Test
    void findByUsuario_existente_deberiaRetornar() {
        when(usuarioRepository.findByUsuario("admin")).thenReturn(Optional.of(usuario));
        assertThat(usuarioService.findByUsuario("admin")).isPresent();
    }

    // ========== contarUsuarios ==========

    @Test
    void contarUsuarios_deberiaDelegar() {
        when(usuarioRepository.countByEstadoNot(2)).thenReturn(3L);
        assertThat(usuarioService.contarUsuarios()).isEqualTo(3L);
    }
}
