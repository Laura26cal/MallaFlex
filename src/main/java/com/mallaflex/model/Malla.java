package com.mallaflex.model;

import java.util.List;

public record Malla(
    Carrera carrera,
    Integer creditosTotales,
    List<MallaSemestre> semestres
) {}