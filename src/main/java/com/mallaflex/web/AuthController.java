package com.mallaflex.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mallaflex.entity.Usuario;
import com.mallaflex.exception.RecursoNoEncontradoException;
import com.mallaflex.exception.ReglaNegocioException;
import com.mallaflex.service.CatalogoConsultaService;
import com.mallaflex.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;
    private final CatalogoConsultaService consulta;

    public AuthController(UsuarioService usuarioService, CatalogoConsultaService consulta) {
        this.usuarioService = usuarioService;
        this.consulta = consulta;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String iniciar(@RequestParam String correo,
                          @RequestParam String password,
                          HttpSession session, Model model) {
        try {
            Usuario usuario = usuarioService.autenticar(correo, password);
            session.setAttribute("usuario", usuario);
            return "redirect:/";
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("carreras", consulta.listarCarreras());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@RequestParam String nombre,
                            @RequestParam String correo,
                            @RequestParam String password,
                            @RequestParam String carrera,
                            Model model) {
        try {
            usuarioService.registrar(new Usuario(null, nombre, correo, password, null, carrera));
            return "redirect:/login?registrado";
        } catch (ReglaNegocioException | RecursoNoEncontradoException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("carreras", consulta.listarCarreras());
            return "registro";
        }
    }

    @PostMapping("/logout")
    public String salir(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}