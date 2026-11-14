package com.mallaflex.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import com.mallaflex.entity.Materia;
import com.mallaflex.service.HistoriaService;


import com.mallaflex.service.CatalogoConsultaService;

@Controller
public class PaginasController {

    private static final Long USUARIO_ID = 1L;   

    private final CatalogoConsultaService consulta;
    private final HistoriaService historiaService;

    public PaginasController(CatalogoConsultaService consulta, HistoriaService historiaService) {
        this.consulta = consulta;
        this.historiaService = historiaService;
    }

    @GetMapping("/")
    public String inicio(@RequestParam(required = false) String carrera, Model model) {
        model.addAttribute("carreras", consulta.listarCarreras());
        model.addAttribute("malla", consulta.obtenerMalla(resolverCarrera(carrera)));
        return "index";
    }

    @GetMapping("/malla")
    public String malla(@RequestParam(required = false) String carrera, Model model) {
        model.addAttribute("carreras", consulta.listarCarreras());
        model.addAttribute("malla", consulta.obtenerMalla(resolverCarrera(carrera)));
        return "malla";
    }

   
   @GetMapping("/historia")
    public String historia(Model model) {
        model.addAttribute("materias", consulta.listarMaterias());
        model.addAttribute("filas", historiaService.listarHistoria(USUARIO_ID).stream()
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
                                @RequestParam Integer matricula) {
        historiaService.registrarMateria(USUARIO_ID, materiaCodigo, matricula, nota);
        return "redirect:/historia";
    }

    @GetMapping("/ruta")
    public String ruta() { return "ruta"; }

    // si no viene carrera en la URL, usa la primera
    private String resolverCarrera(String carrera) {
        if (carrera != null && !carrera.isBlank()) return carrera;
        return consulta.listarCarreras().get(0).codigo();
    }
}