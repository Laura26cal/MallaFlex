package com.mallaflex.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mallaflex.service.CatalogoConsultaService;

@Controller
public class PaginasController {

    private final CatalogoConsultaService consulta;

    public PaginasController(CatalogoConsultaService consulta) {
        this.consulta = consulta;
    }

    @GetMapping("/")
    public String inicio(@RequestParam(required = false) String carrera, Model model) {
        model.addAttribute("carreras", consulta.listarCarreras());
        model.addAttribute("malla", consulta.obtenerMalla(resolverCarrera(carrera)));
        return "index";
    }

    @GetMapping("/malla")
    public String malla(@RequestParam(required = false) String carrera, Model model) {
        model.addAttribute("malla", consulta.obtenerMalla(resolverCarrera(carrera)));
        return "malla";
    }

   @GetMapping("/historia")
    public String historia(Model model) {
        model.addAttribute("materias", consulta.listarMaterias());
        return "historia";
    }

    @GetMapping("/ruta")
    public String ruta() { return "ruta"; }

    // si no viene carrera en la URL, usa la primera
    private String resolverCarrera(String carrera) {
        if (carrera != null && !carrera.isBlank()) return carrera;
        return consulta.listarCarreras().get(0).codigo();
    }
}