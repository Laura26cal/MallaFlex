package com.mallaflex.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mallaflex.entity.Materia;
import com.mallaflex.entity.Usuario;
import com.mallaflex.service.CatalogoConsultaService;
import com.mallaflex.service.HistoriaService;

import jakarta.servlet.http.HttpSession;

@Controller
public class PaginasController {   

    private final CatalogoConsultaService consulta;
    private final HistoriaService historiaService;

    public PaginasController(CatalogoConsultaService consulta, HistoriaService historiaService) {
        this.consulta = consulta;
        this.historiaService = historiaService;
    }

    @GetMapping("/")
    public String inicio(@RequestParam(required = false) String carrera, HttpSession session, Model model) {
        Usuario usuario = usuarioEnSesion(session);
        cargarDatosSesion(model, usuario);
        model.addAttribute("carreras", consulta.listarCarreras());
        model.addAttribute("malla", consulta.obtenerMalla(resolverCarrera(carrera, usuario)));
        return "index";
    }

    @GetMapping("/malla")
    public String malla(@RequestParam(required = false) String carrera, HttpSession session, Model model) {
        Usuario usuario = usuarioEnSesion(session);
        cargarDatosSesion(model, usuario);
        model.addAttribute("carreras", consulta.listarCarreras());
        model.addAttribute("malla", consulta.obtenerMalla(resolverCarrera(carrera, usuario)));
        return "malla";
    }

   
   @GetMapping("/historia")
    public String historia(HttpSession session, Model model) {
        Usuario usuario = usuarioEnSesion(session);
        if (usuario == null) {
            return "redirect:/login";
        }
        cargarDatosSesion(model, usuario);
        model.addAttribute("materias", consulta.listarMaterias());
        model.addAttribute("filas", historiaService.listarHistoria(usuario.id()).stream()
            .map(h -> {
                Materia m = consulta.obtenerMateriaBasica(h.materiaCodigo());
                return new FilaHistoria(m.nombre(), m.codigo(), m.creditos(),
                                        h.numeroMatricula(), h.nota(), h.estado());
            })
            .toList());
            return "historia";
    }

    @PostMapping("/historia")
    public String guardarHistoria(@RequestParam String materiaCodigo,
                                @RequestParam(required = false) Double nota,
                                @RequestParam Integer matricula,
                                HttpSession session) {
        Usuario usuario = usuarioEnSesion(session);
        if (usuario == null) {
            return "redirect:/login";
        }
        historiaService.registrarMateria(usuario.id(), materiaCodigo, matricula, nota);
        return "redirect:/historia";
    }

    @GetMapping("/ruta")
    public String ruta(HttpSession session, Model model) {
        Usuario usuario = usuarioEnSesion(session);
        if (usuario == null) {
            return "redirect:/login";
        }
        cargarDatosSesion(model, usuario);
        return "ruta";
     }

    @GetMapping ("/admin")
    public String admin(HttpSession session, Model model) {
        Usuario usuario = usuarioEnSesion(session);
        if (!esAdmin(usuario)) {
            return "redirect:/";
        }

        cargarDatosSesion(model, usuario);
        model.addAttribute("carreras", consulta.listarCarreras());
        model.addAttribute("materias", consulta.listarMaterias());
        return "admin";
    }

    private Usuario usuarioEnSesion(HttpSession session) {
        return (Usuario) session.getAttribute("usuario");
    }
    private boolean esAdmin(Usuario usuario) {
        return usuario != null && "ADMIN".equals(usuario.rol());
    }
    private void cargarDatosSesion(Model model, Usuario usuario) {
        model.addAttribute("usuario", usuario);
        model.addAttribute("esAdmin", esAdmin(usuario));
    }
    // si no viene carrera en la URL, usa la primera
    private String resolverCarrera(String carrera, Usuario usuario) {
        if (carrera != null && !carrera.isBlank()) return carrera;
        if (usuario != null && usuario.carreraCodigo() != null && !usuario.carreraCodigo().isBlank()) {
            return usuario.carreraCodigo();
        }
        return consulta.listarCarreras().get(0).codigo();
    }
}