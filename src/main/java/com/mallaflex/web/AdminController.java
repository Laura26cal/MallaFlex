package com.mallaflex.web;

import java.text.Normalizer;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mallaflex.entity.Carrera;
import com.mallaflex.entity.MallaCarrera;
import com.mallaflex.entity.Materia;
import com.mallaflex.entity.MateriaDetalle;
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
    public String raiz() {
        return "redirect:/admin/mallas";
    }

    // ---------- lista de mallas ----------

    @GetMapping("/mallas")
    public String listaMallas(@RequestParam(required = false) String q,
                              HttpSession session, Model model) {
        Usuario u = adminEnSesion(session);
        if (u == null) return "redirect:/";
        datosSesion(model, u);
        model.addAttribute("q", q);
        model.addAttribute("carreras", consulta.listarCarreras().stream()
                .filter(c -> coincide(q, c.codigo(), c.nombre())).toList());
        model.addAttribute("conteo", consulta.contarMateriasPorCarrera());
        return "admin-mallas";
    }

    // ---------- crear malla ----------

    @GetMapping("/mallas/nueva")
    public String nuevaMalla(HttpSession session, Model model) {
        Usuario u = adminEnSesion(session);
        if (u == null) return "redirect:/";
        datosSesion(model, u);
        model.addAttribute("nueva", true);
        model.addAttribute("carrera", new Carrera("", "", "", null, null, 0));
        return "admin-malla";
    }

    @PostMapping("/mallas")
    public String crearMalla(@RequestParam String codigo,
                             @RequestParam String nombre,
                             @RequestParam String plan,
                             @RequestParam Integer creditosTotales,
                             @RequestParam Integer numSemestres,
                             @RequestParam(required = false) Integer creditosLibreEleccion,
                             HttpSession session, RedirectAttributes flash) {
        if (adminEnSesion(session) == null) return "redirect:/";
        String cod = codigo.trim();
        return ejecutar(flash, "Malla " + cod + " creada. Ya puedes agregarle materias.",
                "/admin/mallas/" + cod, "/admin/mallas/nueva",
                () -> admin.crearCarrera(new Carrera(cod, nombre, plan, creditosTotales,
                        numSemestres, creditosLibreEleccion)));
    }

    // ---------- editor de una malla ----------

    @GetMapping("/mallas/{codigo}")
    public String editarMalla(@PathVariable String codigo,
                              @RequestParam(required = false) String q,
                              HttpSession session, Model model) {
        Usuario u = adminEnSesion(session);
        if (u == null) return "redirect:/";
        datosSesion(model, u);

        MallaCarrera malla = consulta.obtenerMalla(codigo);
        model.addAttribute("nueva", false);
        model.addAttribute("carrera", malla.carrera());
        model.addAttribute("malla", malla);
        model.addAttribute("q", q);

        // búsqueda de materias existentes que aún no están en esta malla
        if (q != null && !q.isBlank()) {
            Set<String> enMalla = malla.semestres().stream()
                    .flatMap(s -> s.materias().stream())
                    .map(MateriaDetalle::codigo)
                    .collect(Collectors.toSet());
            model.addAttribute("resultados", consulta.listarMaterias().stream()
                    .filter(m -> !enMalla.contains(m.codigo()))
                    .filter(m -> coincide(q, m.codigo(), m.nombre()))
                    .limit(20).toList());
        }
        return "admin-malla";
    }

    @PostMapping("/mallas/{codigo}/datos")
    public String guardarDatos(@PathVariable String codigo,
                               @RequestParam String nombre,
                               @RequestParam String plan,
                               @RequestParam Integer creditosTotales,
                               @RequestParam Integer numSemestres,
                               @RequestParam(required = false) Integer creditosLibreEleccion,
                               HttpSession session, RedirectAttributes flash) {
        if (adminEnSesion(session) == null) return "redirect:/";
        String destino = "/admin/mallas/" + codigo;
        return ejecutar(flash, "Datos de la malla guardados.", destino, destino,
                () -> admin.actualizarCarrera(new Carrera(codigo, nombre, plan, creditosTotales,
                        numSemestres, creditosLibreEleccion)));
    }

    @PostMapping("/mallas/{codigo}/eliminar")
    public String eliminarMalla(@PathVariable String codigo,
                                HttpSession session, RedirectAttributes flash) {
        if (adminEnSesion(session) == null) return "redirect:/";
        return ejecutar(flash, "Malla " + codigo + " eliminada.", "/admin/mallas", "/admin/mallas",
                () -> admin.eliminarCarrera(codigo));
    }

    // ---------- materias dentro de la malla ----------

    @PostMapping("/mallas/{codigo}/materias/agregar")
    public String agregarExistente(@PathVariable String codigo,
                                   @RequestParam String materiaCodigo,
                                   @RequestParam(required = false) Integer semestre,
                                   @RequestParam(required = false) String tipologia,
                                   HttpSession session, RedirectAttributes flash) {
        if (adminEnSesion(session) == null) return "redirect:/";
        String destino = "/admin/mallas/" + codigo;
        return ejecutar(flash, "Materia " + materiaCodigo + " agregada a la malla.", destino, destino,
                () -> admin.asignarMateriaACarrera(codigo, materiaCodigo, vacioANulo(tipologia),
                        semestre, false));
    }

    @PostMapping("/mallas/{codigo}/materias/{materia}/quitar")
    public String quitarMateria(@PathVariable String codigo, @PathVariable String materia,
                                HttpSession session, RedirectAttributes flash) {
        if (adminEnSesion(session) == null) return "redirect:/";
        String destino = "/admin/mallas/" + codigo;
        return ejecutar(flash, "Materia " + materia + " quitada de la malla.", destino, destino,
                () -> admin.quitarMateriaDeCarrera(codigo, materia));
    }

    @GetMapping("/mallas/{codigo}/materias/nueva")
    public String formNuevaMateria(@PathVariable String codigo,
                                   @RequestParam(required = false) Integer semestre,
                                   HttpSession session, Model model) {
        Usuario u = adminEnSesion(session);
        if (u == null) return "redirect:/";
        datosSesion(model, u);
        cargarCatalogo(model, consulta.obtenerCarrera(codigo));
        model.addAttribute("nueva", true);
        model.addAttribute("codigoMateria", "");
        model.addAttribute("nombre", "");
        model.addAttribute("creditos", null);
        model.addAttribute("tipologia", "");
        model.addAttribute("semestre", semestre);
        model.addAttribute("requiereMallaCompleta", false);
        model.addAttribute("prerrequisitos", List.of());
        model.addAttribute("correquisitos", List.of());
        return "admin-materia";
    }

    @PostMapping("/mallas/{codigo}/materias/nueva")
    public String crearMateriaEnMalla(@PathVariable String codigo,
                                      @RequestParam String codigoMateria,
                                      @RequestParam String nombre,
                                      @RequestParam Integer creditos,
                                      @RequestParam(required = false) Integer semestre,
                                      @RequestParam(required = false) String tipologia,
                                      @RequestParam(defaultValue = "false") boolean requiereMallaCompleta,
                                      @RequestParam(required = false) List<String> prerrequisitos,
                                      @RequestParam(required = false) List<String> correquisitos,
                                      HttpSession session, RedirectAttributes flash) {
        if (adminEnSesion(session) == null) return "redirect:/";
        String destino = "/admin/mallas/" + codigo;
        return ejecutar(flash, "Materia " + codigoMateria.trim() + " creada y agregada a la malla.",
                destino, destino + "/materias/nueva",
                () -> admin.crearMateriaEnMalla(codigo, new Materia(codigoMateria, nombre, creditos),
                        prerrequisitos, correquisitos, vacioANulo(tipologia), semestre,
                        requiereMallaCompleta));
    }

    @GetMapping("/mallas/{codigo}/materias/{materia}")
    public String formEditarMateria(@PathVariable String codigo, @PathVariable String materia,
                                    HttpSession session, Model model) {
        Usuario u = adminEnSesion(session);
        if (u == null) return "redirect:/";
        datosSesion(model, u);
        MateriaDetalle d = consulta.obtenerMateria(codigo, materia);
        cargarCatalogo(model, consulta.obtenerCarrera(codigo));
        model.addAttribute("nueva", false);
        model.addAttribute("codigoMateria", d.codigo());
        model.addAttribute("nombre", d.nombre());
        model.addAttribute("creditos", d.creditos());
        model.addAttribute("tipologia", d.tipologia() == null ? "" : d.tipologia());
        model.addAttribute("semestre", d.semestreSugerido());
        model.addAttribute("requiereMallaCompleta", d.requiereMallaCompleta());
        model.addAttribute("prerrequisitos", d.prerrequisitos());
        model.addAttribute("correquisitos", d.correquisitos());
        return "admin-materia";
    }

    @PostMapping("/mallas/{codigo}/materias/{materia}")
    public String guardarMateria(@PathVariable String codigo, @PathVariable String materia,
                                 @RequestParam String nombre,
                                 @RequestParam Integer creditos,
                                 @RequestParam(required = false) Integer semestre,
                                 @RequestParam(required = false) String tipologia,
                                 @RequestParam(defaultValue = "false") boolean requiereMallaCompleta,
                                 @RequestParam(required = false) List<String> prerrequisitos,
                                 @RequestParam(required = false) List<String> correquisitos,
                                 HttpSession session, RedirectAttributes flash) {
        if (adminEnSesion(session) == null) return "redirect:/";
        String destino = "/admin/mallas/" + codigo;
        return ejecutar(flash, "Materia " + materia + " guardada.", destino,
                destino + "/materias/" + materia,
                () -> admin.guardarMateriaEnMalla(codigo, new Materia(materia, nombre, creditos),
                        vacioANulo(tipologia), semestre, requiereMallaCompleta,
                        prerrequisitos, correquisitos));
    }

    //estadísticas

    @GetMapping("/estadisticas")
    public String verEstadisticas(HttpSession session, Model model) {
        Usuario u = adminEnSesion(session);
        if (u == null) return "redirect:/";
        datosSesion(model, u);
        return "admin-estadisticas";
    }


    /** Corre la acción; los errores de negocio se muestran como alerta en vez de romper la página. */
    private String ejecutar(RedirectAttributes flash, String mensajeOk, String destinoOk,
                            String destinoError, Runnable accion) {
        try {
            accion.run();
            flash.addFlashAttribute("ok", mensajeOk);
            return "redirect:" + destinoOk;
        } catch (ReglaNegocioException | RecursoNoEncontradoException e) {
            flash.addFlashAttribute("error", e.getMessage());
        } catch (DataIntegrityViolationException e) {
            flash.addFlashAttribute("error", "No se pudo completar: hay datos que dependen de este "
                    + "registro (por ejemplo, estudiantes inscritos en la carrera).");
        }
        return "redirect:" + destinoError;
    }

    private void cargarCatalogo(Model model, Carrera carrera) {
        List<Materia> todas = consulta.listarMaterias();
        model.addAttribute("carrera", carrera);
        model.addAttribute("materias", todas);
        model.addAttribute("nombres", todas.stream()
                .collect(Collectors.toMap(Materia::codigo, Materia::nombre)));
    }

    private void datosSesion(Model model, Usuario u) {
        model.addAttribute("usuario", u);
        model.addAttribute("esAdmin", true);
    }

    private Usuario adminEnSesion(HttpSession session) {
        Usuario u = (Usuario) session.getAttribute("usuario");
        return (u != null && "ADMIN".equals(u.rol())) ? u : null;
    }

    private String vacioANulo(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }

    // búsqueda sin importar mayúsculas ni tildes: "calculo" encuentra "Cálculo"
    private boolean coincide(String q, String... campos) {
        if (q == null || q.isBlank()) return true;
        String buscado = normalizar(q);
        for (String campo : campos) {
            if (campo != null && normalizar(campo).contains(buscado)) return true;
        }
        return false;
    }

    private String normalizar(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase().trim();
    }
}