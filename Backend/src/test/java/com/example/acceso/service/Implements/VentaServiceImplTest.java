package com.example.acceso.service.Implements;

import com.example.acceso.DTO.CuotasProgramadasDTO;
import com.example.acceso.DTO.DetalleVentaDTO;
import com.example.acceso.DTO.PagosDTO;
import com.example.acceso.DTO.VentaDTO;
import com.example.acceso.model.*;
import com.example.acceso.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceImplTest {

    @Mock private VentaRepository ventaRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private CuotaRepository cuotaRepository;
    @Mock private PagoRepository pagoRepository;
    @Mock private SerieComprobanteRepository serieComprobanteRepository;
    @Mock private FormaPagoRepository formaPagoRepository;
    @Mock private ProductoRepository productoRepository;

    @InjectMocks
    private VentaServiceImpl ventaService;

    private Cliente cliente;
    private Usuario usuario;
    private Producto producto;
    private SerieComprobante serie;
    private FormaPago formaPagoContado;
    private FormaPago formaPagoCredito;
    private VentaDTO ventaContadoDTO;
    private VentaDTO ventaCreditoDTO;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Cliente Test");
        cliente.setDocumento("12345678");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsuario("vendedor");

        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Producto Test");
        producto.setPrecioVenta(100.0);
        producto.setStock(10);

        serie = new SerieComprobante();
        serie.setId(1L);
        serie.setSerie("B001");
        serie.setCorrelativo_actual(0);

        formaPagoContado = new FormaPago();
        formaPagoContado.setId(1L);
        formaPagoContado.setNombre("Contado");

        formaPagoCredito = new FormaPago();
        formaPagoCredito.setId(2L);
        formaPagoCredito.setNombre("Credito");

        ventaContadoDTO = new VentaDTO();
        ventaContadoDTO.setClienteId(1L);
        ventaContadoDTO.setUsuarioId(1L);
        ventaContadoDTO.setSerieComprobanteId(1L);
        ventaContadoDTO.setFormaPagoId(1L);
        ventaContadoDTO.setDetalles(List.of(
                new DetalleVentaDTO(1L, 2)
        ));

        ventaCreditoDTO = new VentaDTO();
        ventaCreditoDTO.setClienteId(1L);
        ventaCreditoDTO.setUsuarioId(1L);
        ventaCreditoDTO.setSerieComprobanteId(1L);
        ventaCreditoDTO.setFormaPagoId(2L);
        ventaCreditoDTO.setMontoInicial(BigDecimal.valueOf(50));
        ventaCreditoDTO.setDetalles(List.of(
                new DetalleVentaDTO(1L, 2)
        ));
        ventaCreditoDTO.setPlanDeCuotas(List.of(
                new CuotasProgramadasDTO(BigDecimal.valueOf(100), LocalDate.now().plusMonths(1)),
                new CuotasProgramadasDTO(BigDecimal.valueOf(50), LocalDate.now().plusMonths(2))
        ));
    }

    // ========== crearVenta - Contado ==========

    @Test
    void crearVenta_contado_deberiaDescontarStockYGuardar() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(formaPagoRepository.findById(1L)).thenReturn(Optional.of(formaPagoContado));
        when(serieComprobanteRepository.findByIdWithLock(1L)).thenReturn(Optional.of(serie));
        when(productoRepository.findByIdWithLock(1L)).thenReturn(Optional.of(producto));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Venta result = ventaService.crearVenta(ventaContadoDTO);

        assertThat(result.getTotal()).isEqualByComparingTo(BigDecimal.valueOf(200).setScale(2));
        assertThat(result.getDeuda()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getEstado()).isEqualTo(1);
        assertThat(producto.getStock()).isEqualTo(8);
        assertThat(serie.getCorrelativo_actual()).isEqualTo(1);
        verify(ventaRepository).save(any(Venta.class));
    }

    @Test
    void crearVenta_contado_deberiaIncrementarCorrelativo() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(formaPagoRepository.findById(1L)).thenReturn(Optional.of(formaPagoContado));
        when(serieComprobanteRepository.findByIdWithLock(1L)).thenReturn(Optional.of(serie));
        when(productoRepository.findByIdWithLock(1L)).thenReturn(Optional.of(producto));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Venta result = ventaService.crearVenta(ventaContadoDTO);

        assertThat(result.getCorrelativo()).isEqualTo(1);
        verify(serieComprobanteRepository).findByIdWithLock(1L);
    }

    // ========== crearVenta - Credito ==========

    @Test
    void crearVenta_credito_deberiaCrearCuotas() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(formaPagoRepository.findById(2L)).thenReturn(Optional.of(formaPagoCredito));
        when(serieComprobanteRepository.findByIdWithLock(1L)).thenReturn(Optional.of(serie));
        when(productoRepository.findByIdWithLock(1L)).thenReturn(Optional.of(producto));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Venta result = ventaService.crearVenta(ventaCreditoDTO);

        assertThat(result.getDeuda()).isEqualByComparingTo(BigDecimal.valueOf(150));
        assertThat(result.getEstado()).isEqualTo(0);
        assertThat(result.getCuotas()).hasSize(2);
        assertThat(result.getCuotas().get(0).getMonto()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(result.getCuotas().get(1).getMonto()).isEqualByComparingTo(BigDecimal.valueOf(50));
    }

    @Test
    void crearVenta_credito_inconsistenciaDeberiaLanzarExcepcion() {
        ventaCreditoDTO.setMontoInicial(BigDecimal.valueOf(10));
        ventaCreditoDTO.setPlanDeCuotas(List.of(
                new CuotasProgramadasDTO(BigDecimal.valueOf(30), LocalDate.now().plusMonths(1))
        ));

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(formaPagoRepository.findById(2L)).thenReturn(Optional.of(formaPagoCredito));
        when(serieComprobanteRepository.findByIdWithLock(1L)).thenReturn(Optional.of(serie));
        when(productoRepository.findByIdWithLock(1L)).thenReturn(Optional.of(producto));

        assertThatThrownBy(() -> ventaService.crearVenta(ventaCreditoDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("consistencia");
    }

    // ========== crearVenta - Errores ==========

    @Test
    void crearVenta_clienteNoEncontrado_deberiaLanzarExcepcion() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ventaService.crearVenta(ventaContadoDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("El cliente no existe");
    }

    @Test
    void crearVenta_stockInsuficiente_deberiaLanzarExcepcion() {
        producto.setStock(1);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(formaPagoRepository.findById(1L)).thenReturn(Optional.of(formaPagoContado));
        when(serieComprobanteRepository.findByIdWithLock(1L)).thenReturn(Optional.of(serie));
        when(productoRepository.findByIdWithLock(1L)).thenReturn(Optional.of(producto));

        assertThatThrownBy(() -> ventaService.crearVenta(ventaContadoDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Stock insuficiente");
    }

    // ========== anularVenta ==========

    @Test
    void anularVenta_ventaActiva_deberiaRestaurarStockYMarcarAnulado() {
        Venta venta = crearVentaPersistida();
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Venta result = ventaService.anularVenta(1L);

        assertThat(result.getEstado()).isEqualTo(2);
        assertThat(producto.getStock()).isEqualTo(12);
        verify(ventaRepository).save(venta);
    }

    @Test
    void anularVenta_ventaYaAnulada_deberiaLanzarExcepcion() {
        Venta venta = crearVentaPersistida();
        venta.setEstado(2);
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        assertThatThrownBy(() -> ventaService.anularVenta(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ya se encuentra eliminada");
    }

    @Test
    void anularVenta_ventaNoEncontrada_deberiaLanzarExcepcion() {
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ventaService.anularVenta(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no existe");
    }

    // ========== registrarPago ==========

    @Test
    void registrarPago_pagoParcial_deberiaActualizarSaldoCuota() {
        Venta venta = crearVentaPersistida();
        Cuota cuota = new Cuota();
        cuota.setId(1L);
        cuota.setSaldo(BigDecimal.valueOf(100));
        cuota.setEstado(0);
        cuota.setVenta(venta);

        PagosDTO pago = new PagosDTO();
        pago.setCuotaId(1L);
        pago.setMontoPagado(BigDecimal.valueOf(40));
        pago.setMetodoPago("EFECTIVO");

        when(cuotaRepository.findById(1L)).thenReturn(Optional.of(cuota));
        when(pagoRepository.save(any(Pago.class))).thenReturn(new Pago());

        ventaService.registrarPago(pago);

        assertThat(cuota.getSaldo()).isEqualByComparingTo(BigDecimal.valueOf(60));
        assertThat(cuota.getEstado()).isEqualTo(0);
    }

    @Test
    void registrarPago_pagoCompleto_deberiaMarcarCuotaPagada() {
        Venta venta = crearVentaPersistida();
        Cuota cuota = new Cuota();
        cuota.setId(1L);
        cuota.setSaldo(BigDecimal.valueOf(100));
        cuota.setEstado(0);
        cuota.setVenta(venta);

        PagosDTO pago = new PagosDTO();
        pago.setCuotaId(1L);
        pago.setMontoPagado(BigDecimal.valueOf(100));
        pago.setMetodoPago("YAPE");

        when(cuotaRepository.findById(1L)).thenReturn(Optional.of(cuota));
        when(pagoRepository.save(any(Pago.class))).thenReturn(new Pago());

        ventaService.registrarPago(pago);

        assertThat(cuota.getSaldo()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(cuota.getEstado()).isEqualTo(1);
    }

    @Test
    void registrarPago_montoExcedeSaldo_deberiaLanzarExcepcion() {
        Cuota cuota = new Cuota();
        cuota.setId(1L);
        cuota.setSaldo(BigDecimal.valueOf(50));
        cuota.setEstado(0);

        PagosDTO pago = new PagosDTO();
        pago.setCuotaId(1L);
        pago.setMontoPagado(BigDecimal.valueOf(100));

        when(cuotaRepository.findById(1L)).thenReturn(Optional.of(cuota));

        assertThatThrownBy(() -> ventaService.registrarPago(pago))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no puede ser mayor");
    }

    // ========== listarVentas ==========

    @Test
    void listarVentas_deberiaRetornarSoloVentasActivas() {
        when(ventaRepository.findAllByEstadoNot(2)).thenReturn(List.of(new Venta(), new Venta()));

        List<Venta> result = ventaService.listarVentas();

        assertThat(result).hasSize(2);
        verify(ventaRepository).findAllByEstadoNot(2);
    }

    // ========== obtenerVenta ==========

    @Test
    void obtenerVenta_idValido_deberiaRetornarVenta() {
        Venta venta = new Venta();
        venta.setId(1L);
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        Venta result = ventaService.obtenerVenta(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void obtenerVenta_idInvalido_deberiaLanzarExcepcion() {
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ventaService.obtenerVenta(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no existe");
    }

    // ========== helpers ==========

    private Venta crearVentaPersistida() {
        Venta venta = new Venta();
        venta.setId(1L);
        venta.setTotal(BigDecimal.valueOf(200));
        venta.setDeuda(BigDecimal.valueOf(200));
        venta.setEstado(1);

        DetalleVenta detalle = new DetalleVenta();
        detalle.setProducto(producto);
        detalle.setCantidad(2);
        detalle.setVenta(venta);
        venta.setDetalleVentas(new ArrayList<>(List.of(detalle)));

        Cuota cuota = new Cuota();
        cuota.setId(1L);
        cuota.setVenta(venta);
        cuota.setSaldo(BigDecimal.valueOf(200));
        cuota.setEstado(0);
        venta.setCuotas(new ArrayList<>(List.of(cuota)));

        return venta;
    }
}
