package com.mallaflex.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaginasController {

    @GetMapping("/")
    public String inicio() { return "index"; }

    @GetMapping("/historia")
    public String historia() { return "historia"; }

    @GetMapping("/malla")
    public String malla() { return "malla"; }

    @GetMapping("/ruta")
    public String ruta() { return "ruta"; }
}
