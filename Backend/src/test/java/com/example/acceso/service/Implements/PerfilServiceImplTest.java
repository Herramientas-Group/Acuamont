package com.example.acceso.service.Implements;

import com.example.acceso.model.Opcion;
import com.example.acceso.model.Perfil;
import com.example.acceso.repository.OpcionRepository;
import com.example.acceso.repository.PerfilRepository;
import com.example.acceso.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PerfilServiceImplTest {

    @Mock private PerfilRepository perfilRepository;
    @Mock private OpcionRepository opcionRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @InjectMocks private PerfilServiceImpl perfilService;

    private Perfil perfil;

    @BeforeEach
    void setUp() {
        perfil = new Perfil();
        perfil.setId(1L);
        perfil.setNombre("Administrador");
        perfil.setEstado(1);
    }

    @Test
    void listarPerfilesActivos_deberiaRetornarSoloEstado1() {
        when(perfilRepository.findByEstado(1)).thenReturn(List.of(perfil));
        assertThat(perfilService.listarPerfilesActivos()).hasSize(1);
    }

    @Test
    void listarTodosLosPerfiles_deberiaExcluirEstado2() {
        when(perfilRepository.findAllByEstadoNot(2)).thenReturn(List.of(perfil));
        assertThat(perfilService.listarTodosLosPerfiles()).hasSize(1);
    }

    @Test
    void guardarPerfil_deberiaDelegar() {
        when(perfilRepository.save(any(Perfil.class))).thenReturn(perfil);
        assertThat(perfilService.guardarPerfil(perfil)).isEqualTo(perfil);
    }

    @Test
    void obtenerPerfilPorId_existente_deberiaRetornar() {
        when(perfilRepository.findById(1L)).thenReturn(Optional.of(perfil));
        assertThat(perfilService.obtenerPerfilPorId(1L)).isPresent();
    }

    @Test
    void cambiarEstadoPerfil_activo_deberiaCambiarAInactivo() {
        when(perfilRepository.findById(1L)).thenReturn(Optional.of(perfil));
        when(perfilRepository.save(any(Perfil.class))).thenReturn(perfil);

        Optional<Perfil> result = perfilService.cambiarEstadoPerfil(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getEstado()).isEqualTo(0);
    }

    @Test
    void cambiarEstadoPerfil_inactivo_deberiaCambiarAActivo() {
        perfil.setEstado(0);
        when(perfilRepository.findById(1L)).thenReturn(Optional.of(perfil));
        when(perfilRepository.save(any(Perfil.class))).thenReturn(perfil);

        Optional<Perfil> result = perfilService.cambiarEstadoPerfil(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getEstado()).isEqualTo(1);
    }

    @Test
    void listarTodasLasOpciones_deberiaDelegar() {
        when(opcionRepository.findAll()).thenReturn(List.of(new Opcion()));
        assertThat(perfilService.listarTodasLasOpciones()).hasSize(1);
    }

    // ========== eliminarPerfil ==========

    @Test
    void eliminarPerfil_sinUsuariosActivos_deberiaHacerSoftDelete() {
        when(perfilRepository.findById(1L)).thenReturn(Optional.of(perfil));
        when(usuarioRepository.countByPerfilAndEstado(perfil, 1)).thenReturn(0L);
        when(perfilRepository.save(any(Perfil.class))).thenReturn(perfil);

        perfilService.eliminarPerfil(1L);

        assertThat(perfil.getEstado()).isEqualTo(2);
        verify(perfilRepository).save(perfil);
    }

    @Test
    void eliminarPerfil_conUsuariosActivos_deberiaLanzarExcepcion() {
        when(perfilRepository.findById(1L)).thenReturn(Optional.of(perfil));
        when(usuarioRepository.countByPerfilAndEstado(perfil, 1)).thenReturn(3L);

        assertThatThrownBy(() -> perfilService.eliminarPerfil(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("asignado")
                .hasMessageContaining("3");
    }

    @Test
    void eliminarPerfil_noEncontrado_deberiaLanzarExcepcion() {
        when(perfilRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> perfilService.eliminarPerfil(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no existe");
    }
}
