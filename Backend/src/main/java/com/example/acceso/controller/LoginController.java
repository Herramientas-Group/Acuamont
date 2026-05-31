package com.example.acceso.controller;

import com.example.acceso.model.Opcion;
import com.example.acceso.model.Usuario;
import com.example.acceso.service.Interfaces.ServicioAutenticacionDosPasos;
import com.example.acceso.service.Interfaces.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Comparator;
import java.util.Optional;

@Controller
public class LoginController {
    private final UsuarioService usuarioService;
    private final ServicioAutenticacionDosPasos servicio2FA;
    public LoginController(UsuarioService usuarioService, ServicioAutenticacionDosPasos servicio2FA) {
        this.usuarioService = usuarioService;
        this.servicio2FA = servicio2FA;
    }

    @GetMapping("/login")
    public String mostrarFormularioLogin(HttpSession session) {
        if (session.getAttribute("usuarioLogueado") != null) {
            return "redirect:/";
        }
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String usuario, @RequestParam String clave, @RequestParam String token,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = usuarioService.findByUsuario(usuario);
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/login";
        }
        Usuario usuarioEncontrado = usuarioOpt.get();
        if (usuarioEncontrado.getEstado() != 1) { // 1 = Activo
            redirectAttributes.addFlashAttribute("error", "Este usuario se encuentra inactivo.");
            return "redirect:/login";
        }
        if (usuarioService.verificarContrasena(clave, usuarioEncontrado.getClave())) {
            if (usuarioEncontrado.isUsa2FA()) {
                if (token == null || token.trim().isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "Se requiere el token de seguridad.");
                    return "redirect:/login";
                }
                if (!servicio2FA.esCodigoValido(usuarioEncontrado.getSecreto2FA(), token)) {
                    redirectAttributes.addFlashAttribute("error", "El token de seguridad es incorrecto.");
                    return "redirect:/login";
                }
            }
            session.setAttribute("usuarioLogueado", usuarioEncontrado);
            var opcionesMenu = usuarioEncontrado.getPerfil().getOpciones().stream()
                    .sorted(Comparator.comparing(Opcion::getId))
                    .toList();
            session.setAttribute("menuOpciones", opcionesMenu);
            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("error", "Contraseña incorrecta.");
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("logout", "Has cerrado sesión exitosamente.");
        return "redirect:/login";
    }
}
