package com.example.acceso.service.Implements;

import com.example.acceso.DTO.RedSocialDTO;
import com.example.acceso.model.RedSocial;
import com.example.acceso.repository.RedSocialRepository;
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
class RedSocialServiceImplTest {

    @Mock private RedSocialRepository redSocialRepository;
    @InjectMocks private RedSocialServiceImpl redSocialService;

    private RedSocial redSocial;

    @BeforeEach
    void setUp() {
        redSocial = new RedSocial();
        redSocial.setId(1L);
        redSocial.setNombre("Facebook");
        redSocial.setUrl("https://facebook.com/acuamont");
        redSocial.setIcono("facebook_icon");
        redSocial.setOrden("1");
        redSocial.setEstado(1);
    }

    @Test
    void listarRedesSociales_deberiaRetornarTodas() {
        when(redSocialRepository.findAll()).thenReturn(List.of(redSocial));
        assertThat(redSocialService.listarRedesSociales()).hasSize(1);
    }

    @Test
    void listarRedesSocialesActivas_deberiaRetornarSoloEstado1() {
        when(redSocialRepository.findAllByEstado(1)).thenReturn(List.of(redSocial));
        assertThat(redSocialService.listarRedesSocialesActivas()).hasSize(1);
    }

    @Test
    void listarRedesSocialesActivas_sinActivas_deberiaRetornarVacio() {
        when(redSocialRepository.findAllByEstado(1)).thenReturn(List.of());
        assertThat(redSocialService.listarRedesSocialesActivas()).isEmpty();
    }

    @Test
    void actualizarRedSocial_existente_deberiaActualizarUrl() {
        when(redSocialRepository.findById(1L)).thenReturn(Optional.of(redSocial));
        when(redSocialRepository.save(any(RedSocial.class))).thenReturn(redSocial);

        RedSocialDTO dto = new RedSocialDTO("https://facebook.com/nuevo");
        RedSocial result = redSocialService.actualizarRedSocial(1L, dto);

        assertThat(result.getUrl()).isEqualTo("https://facebook.com/nuevo");
        verify(redSocialRepository).save(redSocial);
    }

    @Test
    void actualizarRedSocial_noEncontrado_deberiaLanzarExcepcion() {
        when(redSocialRepository.findById(99L)).thenReturn(Optional.empty());

        RedSocialDTO dto = new RedSocialDTO("https://nueva.url");
        assertThatThrownBy(() -> redSocialService.actualizarRedSocial(99L, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no existe");
        verify(redSocialRepository, never()).save(any());
    }

    @Test
    void cambiarEstadoRedSocial_activo_deberiaCambiarAInactivo() {
        when(redSocialRepository.findById(1L)).thenReturn(Optional.of(redSocial));
        when(redSocialRepository.save(any(RedSocial.class))).thenReturn(redSocial);

        Optional<RedSocial> result = redSocialService.cambiarEstadoRedSocial(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getEstado()).isEqualTo(0);
    }

    @Test
    void cambiarEstadoRedSocial_inactivo_deberiaCambiarAActivo() {
        redSocial.setEstado(0);
        when(redSocialRepository.findById(1L)).thenReturn(Optional.of(redSocial));
        when(redSocialRepository.save(any(RedSocial.class))).thenReturn(redSocial);

        Optional<RedSocial> result = redSocialService.cambiarEstadoRedSocial(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getEstado()).isEqualTo(1);
    }

    @Test
    void cambiarEstadoRedSocial_idNulo_deberiaRetornarEmpty() {
        assertThat(redSocialService.cambiarEstadoRedSocial(null)).isEmpty();
        verify(redSocialRepository, never()).findById(any());
    }

    @Test
    void cambiarEstadoRedSocial_idInvalido_deberiaRetornarEmpty() {
        assertThat(redSocialService.cambiarEstadoRedSocial(-1L)).isEmpty();
        verify(redSocialRepository, never()).findById(any());
    }
}
