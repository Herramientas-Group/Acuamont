package com.example.acceso.controller;

import com.example.acceso.model.Cliente;
import com.example.acceso.service.Interfaces.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/clientes")
public class ClienteController {
    private final ClienteService clienteService;
    private final RestTemplate restTemplate;

    @Value("${documento.tokenCode}")
    private String tokenCode;

    @Value("${documento.urlDni}")
    private String urlDni;

    @Value("${documento.urlRuc}")
    private String urlRuc;

    public ClienteController(ClienteService clienteService, RestTemplate restTemplate) {
        this.clienteService = clienteService;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/api/listar")
    @PreAuthorize("hasAuthority('OPCION_7')")
    public ResponseEntity<?> listarClientesApi() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", clienteService.listarClientes());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/guardar")
    @PreAuthorize("hasAuthority('OPCION_7')")
    public ResponseEntity<?> guardarClienteApi(@Valid @RequestBody Cliente cliente, BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();
        if(bindingResult.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));
            response.put("success", false);
            response.put("message", "Datos inválidos");
            response.put("errors", errores);
            return ResponseEntity.badRequest().body(response);
        }
        try {
            Cliente clienteGuardado = clienteService.guardarCliente(cliente);
            response.put("success", true);
            response.put("cliente", clienteGuardado);
            response.put("message",
                    cliente.getId() != null ? "Cliente actualizado correctamente" : "Cliente creado correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al guardar el cliente: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/api/cambiar-estado/{id}")
    @PreAuthorize("hasAuthority('OPCION_7')")
    public ResponseEntity<?> cambiarEstadoClienteAjax(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            return clienteService.cambiarEstadoCliente(id)
                    .map(cliente -> {
                        response.put("success", true);
                        response.put("data", cliente);
                        response.put("message", "Estado del cliente actualizado correctamente");
                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(()->{
                        response.put("success", false);
                        response.put("message", "Cliente no encontrado");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                    });
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al cambiar el estado del cliente: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/api/{id}")
    @PreAuthorize("hasAuthority('OPCION_7')")
    public ResponseEntity<?> obtenerCliente(@PathVariable Long id) {
        try {
            return clienteService.obtenerClientePorId(id).map(cliente -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", cliente);
                return ResponseEntity.ok(response);
            }).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al obtener el cliente: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/api/eliminar/{id}")
    @PreAuthorize("hasAuthority('OPCION_7')")
    public ResponseEntity<?> eliminarCliente(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!clienteService.obtenerClientePorId(id).isPresent()) {
                response.put("success", false);
                response.put("message", "Cliente no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            clienteService.eliminarCliente(id);
            response.put("success", true);
            response.put("message", "Cliente eliminado correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar el cliente: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/api/buscar-cliente-documento/{documento}")
    @PreAuthorize("hasAuthority('OPCION_7')")
    public ResponseEntity<?> buscarPorDocumentoInterno(@PathVariable String documento) {
        return clienteService.obtenerClientePorDocumento(documento)
                .map(cliente -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("data", cliente);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "Cliente no encontrado en la base de datos local.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }

    @GetMapping("/api/buscar-documento/{documento}")
    public ResponseEntity<?> buscarDocumentoExterno(@PathVariable String documento) {
        Map<String, Object> response = new HashMap<>();
        try {
            String url;
            if (documento.length() == 8) {
                url = urlDni + documento;
            } else if (documento.length() == 11) {
                url = urlRuc + documento;
            } else {
                response.put("success", false);
                response.put("message", "Documento inválido");
                return ResponseEntity.badRequest().body(response);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(tokenCode);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> apiResponse = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map.class);

            Map body = apiResponse.getBody();
            if (body == null || body.get("success") != Boolean.TRUE) {
                response.put("success", false);
                response.put("message", "No se encontró información para el documento ingresado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            return ResponseEntity.ok(body);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al consultar el documento: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
