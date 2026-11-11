package com.mallaflex.model;

import java.util.List;

public record MallaCarrera(
    Carrera carrera,
    Integer creditosMalla,
    List<MallaSemestre> semestres
) {}