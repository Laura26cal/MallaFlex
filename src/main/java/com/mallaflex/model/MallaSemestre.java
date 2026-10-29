package com.mallaflex.model;

import java.util.List;

public record MallaSemestre(
    Integer semestre,
    Integer creditos,
    List<MateriaDetalle> materias
) {}