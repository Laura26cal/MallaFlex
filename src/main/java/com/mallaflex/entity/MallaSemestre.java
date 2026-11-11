package com.mallaflex.entity;

import java.util.List;

public record MallaSemestre(
    Integer semestre,
    Integer creditos,
    List<MateriaDetalle> materias
) {}