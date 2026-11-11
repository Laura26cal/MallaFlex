package com.mallaflex.model;
import java.util.List;

public record MateriaDetalle(
        String codigo,
        String nombre,
        Integer creditos,
        String tipologia,
        Integer semestreSugerido,
        boolean requiereMallaCompleta,
        List<String> prerrequisitos,
        List<String> correquisitos
    ) {}
