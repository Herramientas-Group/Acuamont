package com.example.acceso.service.Implements;

import com.example.acceso.model.Cliente;
import com.example.acceso.repository.ClienteRepository;
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
class ClienteServiceImplTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Juan Pérez");
        cliente.setDocumento("12345678");
        cliente.setTelefono("987654321");
        cliente.setCorreo("juan@test.com");
        cliente.setEstado(1);
    }

    // ========== listarClientes ==========

    @Test
    void listarClientes_deberiaRetornarSoloActivos() {
        when(clienteRepository.findAllByEstadoNot(2)).thenReturn(List.of(cliente, new Cliente()));

        List<Cliente> result = clienteService.listarClientes();

        assertThat(result).hasSize(2);
        verify(clienteRepository).findAllByEstadoNot(2);
    }

    @Test
    void listarClientes_sinClientes_deberiaRetornarListaVacia() {
        when(clienteRepository.findAllByEstadoNot(2)).thenReturn(List.of());

        List<Cliente> result = clienteService.listarClientes();

        assertThat(result).isEmpty();
    }

    // ========== guardarCliente ==========

    @Test
    void guardarCliente_nuevo_deberiaGuardarYRetornarCliente() {
        when(clienteRepository.findByDocumento("12345678")).thenReturn(Optional.empty());
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        Cliente result = clienteService.guardarCliente(cliente);

        assertThat(result.getNombre()).isEqualTo("Juan Pérez");
        assertThat(result.getDocumento()).isEqualTo("12345678");
        verify(clienteRepository).save(cliente);
    }

    @Test
    void guardarCliente_existente_deberiaActualizar() {
        Cliente actualizado = new Cliente();
        actualizado.setId(1L);
        actualizado.setNombre("Juan Actualizado");
        actualizado.setDocumento("12345678");

        when(clienteRepository.findByDocumento("12345678")).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(actualizado);

        Cliente result = clienteService.guardarCliente(actualizado);

        assertThat(result.getNombre()).isEqualTo("Juan Actualizado");
        verify(clienteRepository).save(actualizado);
    }

    @Test
    void guardarCliente_documentoDuplicado_deberiaLanzarExcepcion() {
        Cliente otroCliente = new Cliente();
        otroCliente.setId(2L);
        otroCliente.setDocumento("12345678");

        when(clienteRepository.findByDocumento("12345678")).thenReturn(Optional.of(otroCliente));

        assertThatThrownBy(() -> clienteService.guardarCliente(cliente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ya existe un cliente con el mismo documento");
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void guardarCliente_nombreVacio_deberiaLanzarExcepcion() {
        cliente.setNombre("");

        assertThatThrownBy(() -> clienteService.guardarCliente(cliente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El nombre es obligatorio");
    }

    @Test
    void guardarCliente_documentoVacio_deberiaLanzarExcepcion() {
        cliente.setDocumento("");

        assertThatThrownBy(() -> clienteService.guardarCliente(cliente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El documento es obligatorio");
    }

    // ========== obtenerClientePorId ==========

    @Test
    void obtenerClientePorId_idValido_deberiaRetornarCliente() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        Optional<Cliente> result = clienteService.obtenerClientePorId(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getNombre()).isEqualTo("Juan Pérez");
    }

    @Test
    void obtenerClientePorId_idInvalido_deberiaRetornarEmpty() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Cliente> result = clienteService.obtenerClientePorId(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void obtenerClientePorId_idNulo_deberiaRetornarEmpty() {
        Optional<Cliente> result = clienteService.obtenerClientePorId(null);

        assertThat(result).isEmpty();
    }

    // ========== obtenerClientePorDocumento ==========

    @Test
    void obtenerClientePorDocumento_existente_deberiaRetornarCliente() {
        when(clienteRepository.findByDocumento("12345678")).thenReturn(Optional.of(cliente));

        Optional<Cliente> result = clienteService.obtenerClientePorDocumento("12345678");

        assertThat(result).isPresent();
        assertThat(result.get().getDocumento()).isEqualTo("12345678");
    }

    @Test
    void obtenerClientePorDocumento_inexistente_deberiaRetornarEmpty() {
        when(clienteRepository.findByDocumento("99999999")).thenReturn(Optional.empty());

        Optional<Cliente> result = clienteService.obtenerClientePorDocumento("99999999");

        assertThat(result).isEmpty();
    }

    // ========== eliminarCliente ==========

    @Test
    void eliminarCliente_activo_deberiaHacerSoftDelete() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        clienteService.eliminarCliente(1L);

        assertThat(cliente.getEstado()).isEqualTo(2);
        verify(clienteRepository).save(cliente);
    }

    @Test
    void eliminarCliente_noEncontrado_deberiaLanzarExcepcion() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.eliminarCliente(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cliente no encontrado");
    }

    @Test
    void eliminarCliente_idNulo_deberiaLanzarExcepcion() {
        assertThatThrownBy(() -> clienteService.eliminarCliente(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID de cliente inválido");
    }

    // ========== cambiarEstadoCliente ==========

    @Test
    void cambiarEstadoCliente_activo_deberiaCambiarAInactivo() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        Optional<Cliente> result = clienteService.cambiarEstadoCliente(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getEstado()).isEqualTo(0);
    }

    @Test
    void cambiarEstadoCliente_inactivo_deberiaCambiarAActivo() {
        cliente.setEstado(0);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        Optional<Cliente> result = clienteService.cambiarEstadoCliente(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getEstado()).isEqualTo(1);
    }

    @Test
    void cambiarEstadoCliente_idNulo_deberiaLanzarExcepcion() {
        assertThatThrownBy(() -> clienteService.cambiarEstadoCliente(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID de cliente inválido");
    }
}
