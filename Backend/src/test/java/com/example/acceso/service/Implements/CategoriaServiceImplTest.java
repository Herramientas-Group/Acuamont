package com.example.acceso.service.Implements;

import com.example.acceso.model.Categoria;
import com.example.acceso.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceImplTest {

    @Mock private CategoriaRepository categoriaRepository;
    @InjectMocks private CategoriaServiceImpl categoriaService;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Filtros");
        categoria.setEstado(1);
    }

    @Test
    void listarCategorias_deberiaRetornarSoloActivos() {
        when(categoriaRepository.findAllByEstadoNot(2)).thenReturn(List.of(categoria));
        assertThat(categoriaService.listarCategorias()).hasSize(1);
    }

    @Test
    void guardarCategoria_nuevo_deberiaTrimYGuardar() {
        categoria.setId(null);
        categoria.setNombre("  Filtros  ");
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        Categoria result = categoriaService.guardarCategoria(categoria);

        assertThat(result.getNombre()).isEqualTo("Filtros");
        verify(categoriaRepository).save(categoria);
    }

    @Test
    void guardarCategoria_nombreVacio_deberiaLanzarExcepcion() {
        categoria.setNombre("");
        assertThatThrownBy(() -> categoriaService.guardarCategoria(categoria))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nombre");
    }

    @Test
    void guardarCategoria_nombreDuplicado_deberiaCapturarDataIntegrity() {
        categoria.setId(null);
        when(categoriaRepository.save(any(Categoria.class)))
                .thenThrow(new DataIntegrityViolationException("llave duplicada nombre"));

        assertThatThrownBy(() -> categoriaService.guardarCategoria(categoria))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ya existe");
    }

    @Test
    void obtenerCategoriaPorId_valido_deberiaRetornar() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        assertThat(categoriaService.obtenerCategoriaPorId(1L)).isPresent();
    }

    @Test
    void obtenerCategoriaPorId_invalido_deberiaRetornarEmpty() {
        assertThat(categoriaService.obtenerCategoriaPorId(-1L)).isEmpty();
    }

    @Test
    void listarCategoriasActivas_deberiaRetornarEstado1() {
        when(categoriaRepository.findByEstado(1)).thenReturn(List.of(categoria));
        assertThat(categoriaService.listarCategoriasActivas()).hasSize(1);
    }

    @Test
    void obtenerCategoriaPorNombre_deberiaDelegar() {
        when(categoriaRepository.findByNombre("filtros")).thenReturn(Optional.of(categoria));
        assertThat(categoriaService.obtenerCategoriaPorNombre("Filtros")).isPresent();
    }

    @Test
    void eliminarCategoria_activo_deberiaHacerSoftDelete() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        categoriaService.eliminarCategoria(1L);
        assertThat(categoria.getEstado()).isEqualTo(2);
    }

    @Test
    void eliminarCategoria_idNulo_deberiaLanzarExcepcion() {
        assertThatThrownBy(() -> categoriaService.eliminarCategoria(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void eliminarCategoria_noEncontrado_deberiaLanzarExcepcion() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> categoriaService.eliminarCategoria(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no encontrada");
    }

    @Test
    void cambiarEstadoCategoria_activo_deberiaCambiarAInactivo() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        Optional<Categoria> result = categoriaService.cambiarEstadoCategoria(1L);
        assertThat(result).isPresent();
        assertThat(result.get().getEstado()).isEqualTo(0);
    }

    @Test
    void cambiarEstadoCategoria_idNulo_deberiaRetornarEmpty() {
        assertThat(categoriaService.cambiarEstadoCategoria(null)).isEmpty();
    }

    @Test
    void existeCategoria_existente_deberiaRetornarTrue() {
        when(categoriaRepository.existsByNombre("filtros")).thenReturn(true);
        assertThat(categoriaService.existeCategoria("Filtros")).isTrue();
    }

    @Test
    void existeCategoria_nulo_deberiaRetornarFalse() {
        assertThat(categoriaService.existeCategoria(null)).isFalse();
    }

    @Test
    void contarCategorias_deberiaDelegar() {
        when(categoriaRepository.countByEstadoNot(2)).thenReturn(4L);
        assertThat(categoriaService.contarCategorias()).isEqualTo(4L);
    }
}
