package com.example.acceso.controller;

import com.example.acceso.model.Cliente;
import com.example.acceso.service.Interfaces.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/clientes")
public class ClienteController {
    private final ClienteService clienteService;
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/listar")
    public String listarClientes(Model model) {
        List<Cliente> clientes = clienteService.listarClientes();
        model.addAttribute("clientes", clientes);
        model.addAttribute("formCliente", new Cliente());
        return "clientes";
    }

    @GetMapping("/api/listar")
    @ResponseBody
    public ResponseEntity<?> listarClientesApi() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", clienteService.listarClientes());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/guardar")
    @ResponseBody
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
    @ResponseBody
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
    @ResponseBody
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
    @ResponseBody
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
}
