package com.mallaflex.entity;

import java.util.List;

public record RutaSugerida(List<MallaSemestre> semestres, 
Integer semestreGraduacion) {}