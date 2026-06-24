package com.example.acceso.service.Implements;

import com.example.acceso.model.Producto;
import com.example.acceso.repository.ProductoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock private ProductoRepository productoRepository;
    @Mock private CloudinaryService cloudinaryService;
    @InjectMocks private ProductoServiceImpl productoService;

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Filtro Externo");
        producto.setDescripcion("Filtro para acuarios de 200L");
        producto.setPrecioCompra(120.0);
        producto.setPrecioVenta(180.0);
        producto.setStock(15);
        producto.setStockSeguridad(5);
        producto.setImagen("[]");
        producto.setEstado(1);
    }

    // ========== listarProductos ==========

    @Test
    void listarProductos_deberiaRetornarSoloActivos() {
        when(productoRepository.findAllByEstadoNot(2)).thenReturn(List.of(producto, new Producto()));
        assertThat(productoService.listarProductos()).hasSize(2);
    }

    // ========== guardarProducto ==========

    @Test
    void guardarProducto_nuevo_deberiaGuardarConImagenVacia() {
        producto.setId(null);
        Producto saved = new Producto();
        saved.setId(1L);
        saved.setImagen("[]");
        when(productoRepository.save(any(Producto.class))).thenReturn(saved);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(saved));
        when(productoRepository.save(saved)).thenReturn(saved);

        Producto result = productoService.guardarProducto(producto, null);

        assertThat(result.getImagen()).isEqualTo("[]");
        verify(productoRepository, times(2)).save(any(Producto.class));
    }

    @Test
    void guardarProducto_nombreVacio_deberiaLanzarExcepcion() {
        producto.setNombre("");
        assertThatThrownBy(() -> productoService.guardarProducto(producto, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nombre");
    }

    @Test
    void guardarProducto_descripcionVacia_deberiaLanzarExcepcion() {
        producto.setDescripcion("");
        assertThatThrownBy(() -> productoService.guardarProducto(producto, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("descripción");
    }

    @Test
    void guardarProducto_precioCompraNulo_deberiaLanzarExcepcion() {
        producto.setPrecioCompra(null);
        assertThatThrownBy(() -> productoService.guardarProducto(producto, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("compra");
    }

    @Test
    void guardarProducto_precioVentaNulo_deberiaLanzarExcepcion() {
        producto.setPrecioVenta(null);
        assertThatThrownBy(() -> productoService.guardarProducto(producto, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("venta");
    }

    @Test
    void guardarProducto_stockNulo_deberiaLanzarExcepcion() {
        producto.setStock(null);
        assertThatThrownBy(() -> productoService.guardarProducto(producto, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("stock");
    }

    @Test
    void guardarProducto_stockSegNulo_deberiaLanzarExcepcion() {
        producto.setStockSeguridad(null);
        assertThatThrownBy(() -> productoService.guardarProducto(producto, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("seguridad");
    }

    @Test
    void guardarProducto_nombreDuplicado_deberiaCapturarDataIntegrity() {
        producto.setId(null);
        when(productoRepository.save(any(Producto.class)))
                .thenThrow(new DataIntegrityViolationException("llave duplicada nombre"));

        assertThatThrownBy(() -> productoService.guardarProducto(producto, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nombre");
    }

    @Test
    void guardarProducto_conFotos_deberiaSubirlasACloudinary() {
        producto.setId(null);
        MultipartFile foto = mock(MultipartFile.class);
        when(foto.isEmpty()).thenReturn(false);

        Producto saved = new Producto();
        saved.setId(1L);
        saved.setImagen("[]");
        when(productoRepository.save(any(Producto.class))).thenReturn(saved);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(saved));
        when(cloudinaryService.subirImagen(any(MultipartFile.class), anyString())).thenReturn("https://cloudinary.com/img.jpg");

        Producto result = productoService.guardarProducto(producto, List.of(foto));

        assertThat(result.getImagen()).contains("img.jpg");
        verify(cloudinaryService).subirImagen(any(MultipartFile.class), anyString());
    }

    // ========== eliminarProducto ==========

    @Test
    void eliminarProducto_existente_deberiaHacerSoftDeleteYBorrarImagenes() {
        producto.setImagen("[\"https://cloudinary.com/productos_acuamont/1/foto.jpg\"]");
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        productoService.eliminarProducto(1L);

        assertThat(producto.getEstado()).isEqualTo(2);
        assertThat(producto.getImagen()).isNull();
        verify(cloudinaryService).eliminarImagen(anyString(), any());
    }

    @Test
    void eliminarProducto_idInvalido_deberiaLanzarExcepcion() {
        assertThatThrownBy(() -> productoService.eliminarProducto(-1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void eliminarProducto_noEncontrado_deberiaLanzarExcepcion() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> productoService.eliminarProducto(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no encontrado");
    }

    // ========== eliminarImagen ==========

    @Test
    void eliminarImagen_valida_deberiaRemoverDeListaYCloudinary() {
        String url = "https://cloudinary.com/productos_acuamont/1/foto.jpg";
        producto.setImagen("[\"" + url + "\"]");
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        productoService.eliminarImagen(1L, url);

        assertThat(producto.getImagen()).isEqualTo("[]");
        verify(cloudinaryService).eliminarImagen(anyString(), any());
    }

    @Test
    void eliminarImagen_noPerteneciente_deberiaLanzarExcepcion() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        assertThatThrownBy(() -> productoService.eliminarImagen(1L, "https://otra.com/img.jpg"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no pertenece");
    }

    // ========== cambiarEstadoProducto ==========

    @Test
    void cambiarEstadoProducto_deberiaTogglear() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        Optional<Producto> result = productoService.cambiarEstadoProducto(1L);
        assertThat(result).isPresent();
        assertThat(result.get().getEstado()).isEqualTo(0);
    }

    @Test
    void cambiarEstadoProducto_idNulo_deberiaRetornarEmpty() {
        assertThat(productoService.cambiarEstadoProducto(null)).isEmpty();
    }

    // ========== obtenerProductoPorX ==========

    @Test
    void obtenerProductoPorId_valido_deberiaRetornar() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        assertThat(productoService.obtenerProductoPorId(1L)).isPresent();
    }

    @Test
    void obtenerProductoPorId_invalido_deberiaRetornarEmpty() {
        assertThat(productoService.obtenerProductoPorId(-1L)).isEmpty();
    }

    @Test
    void obtenerProductoPorNombre_existente_deberiaRetornar() {
        when(productoRepository.findByNombre("filtro externo")).thenReturn(Optional.of(producto));
        assertThat(productoService.obtenerProductoPorNombre("Filtro Externo")).isPresent();
    }

    // ========== findTop5ProductosMasVendidos ==========

    @Test
    void findTop5ProductosMasVendidos_deberiaDelegarConPageRequest() {
        when(productoRepository.findTop5ProductosMasVendidos(any())).thenReturn(List.of());
        assertThat(productoService.findTop5ProductosMasVendidos()).isEmpty();
    }

    // ========== contarProductos ==========

    @Test
    void contarProductos_deberiaDelegar() {
        when(productoRepository.countByEstadoNot(2)).thenReturn(5L);
        assertThat(productoService.contarProductos()).isEqualTo(5L);
    }
}
