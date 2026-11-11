package com.mallaflex.entity;

import java.util.List;

public record Malla(
    Carrera carrera,
    Integer creditosTotales,
    List<MallaSemestre> semestres
) {}