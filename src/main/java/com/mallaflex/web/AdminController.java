package com.mallaflex.web;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mallaflex.entity.Carrera;
import com.mallaflex.entity.Materia;
import com.mallaflex.entity.Usuario;
import com.mallaflex.exception.RecursoNoEncontradoException;
import com.mallaflex.exception.ReglaNegocioException;
import com.mallaflex.service.CatalogoConsultaService;
import com.mallaflex.service.ICatalogoAdmin;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ICatalogoAdmin admin;
    private final CatalogoConsultaService consulta;

    public AdminController(ICatalogoAdmin admin, CatalogoConsultaService consulta) {
        this.admin = admin;
        this.consulta = consulta;
    }

    @GetMapping
    public String panel(HttpSession session, Model model) {
        Usuario usuario = adminEnSesion(session);
        if (usuario == null) {
            return "redirect:/";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("esAdmin", true);
        model.addAttribute("carreras", consulta.listarCarreras());
        model.addAttribute("materias", consulta.listarMaterias());
        return "admin";
    }

    @PostMapping("/carreras")
    public String crearCarrera(@RequestParam String codigo,
                               @RequestParam String nombre,
                               @RequestParam String plan,
                               @RequestParam Integer creditosTotales,
                               @RequestParam Integer numSemestres,
                               @RequestParam(required = false) Integer creditosLibreEleccion,
                               HttpSession session, RedirectAttributes flash) {
        if (adminEnSesion(session) == null) return "redirect:/";
        return ejecutar(flash, "Carrera " + codigo + " creada.", () ->
            admin.crearCarrera(new Carrera(codigo, nombre, plan, creditosTotales,
                                           numSemestres, creditosLibreEleccion)));
    }

    @PostMapping("/carreras/eliminar")
    public String eliminarCarrera(@RequestParam String codigo,
                                  HttpSession session, RedirectAttributes flash) {
        if (adminEnSesion(session) == null) return "redirect:/";
        return ejecutar(flash, "Carrera " + codigo + " eliminada.", () -> admin.eliminarCarrera(codigo));
    }

    @PostMapping("/materias")
    public String crearMateria(@RequestParam String codigo,
                               @RequestParam String nombre,
                               @RequestParam Integer creditos,
                               @RequestParam(required = false) String prerrequisitos,
                               @RequestParam(required = false) String correquisitos,
                               HttpSession session, RedirectAttributes flash) {
        if (adminEnSesion(session) == null) return "redirect:/";
        return ejecutar(flash, "Materia " + codigo + " creada.", () ->
            admin.crearMateria(new Materia(codigo, nombre, creditos),
                               separar(prerrequisitos), separar(correquisitos)));
    }

    @PostMapping("/materias/eliminar")
    public String eliminarMateria(@RequestParam String codigo,
                                  HttpSession session, RedirectAttributes flash) {
        if (adminEnSesion(session) == null) return "redirect:/";
        return ejecutar(flash, "Materia " + codigo + " eliminada.", () -> admin.eliminarMateria(codigo));
    }

    @PostMapping("/malla/asignar")
    public String asignar(@RequestParam String carreraCodigo,
                          @RequestParam String materiaCodigo,
                          @RequestParam(required = false) String tipologia,
                          @RequestParam(required = false) Integer semestre,
                          @RequestParam(defaultValue = "false") boolean requiereMallaCompleta,
                          HttpSession session, RedirectAttributes flash) {
        if (adminEnSesion(session) == null) return "redirect:/";
        String tip = (tipologia == null || tipologia.isBlank()) ? null : tipologia.trim();
        return ejecutar(flash, "Materia " + materiaCodigo + " asignada a la carrera " + carreraCodigo + ".", () ->
            admin.asignarMateriaACarrera(carreraCodigo, materiaCodigo, tip, semestre, requiereMallaCompleta));
    }

    @PostMapping("/malla/quitar")
    public String quitar(@RequestParam String carreraCodigo,
                         @RequestParam String materiaCodigo,
                         HttpSession session, RedirectAttributes flash) {
        if (adminEnSesion(session) == null) return "redirect:/";
        return ejecutar(flash, "Materia " + materiaCodigo + " quitada de la carrera " + carreraCodigo + ".", () ->
            admin.quitarMateriaDeCarrera(carreraCodigo, materiaCodigo));
    }

    // Corre la acción y deja el resultado como mensaje; los errores de negocio se muestran, no rompen la página
    private String ejecutar(RedirectAttributes flash, String mensajeOk, Runnable accion) {
        try {
            accion.run();
            flash.addFlashAttribute("ok", mensajeOk);
        } catch (ReglaNegocioException | RecursoNoEncontradoException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin";
    }

    private List<String> separar(String codigos) {
        if (codigos == null || codigos.isBlank()) return List.of();
        return Arrays.stream(codigos.split("[,;\\s]+")).filter(c -> !c.isBlank()).toList();
    }

    private Usuario adminEnSesion(HttpSession session) {
        Usuario u = (Usuario) session.getAttribute("usuario");
        return (u != null && "ADMIN".equals(u.rol())) ? u : null;
    }
}