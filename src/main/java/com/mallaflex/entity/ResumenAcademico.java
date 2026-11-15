package com.mallaflex.entity;

import java.util.List;

public record ResumenAcademico(
    Double papa, Integer materiasAprobadas, Integer materiasCursando,
    Integer materiasPerdidas, Integer creditosAprobados,
    Double porcentajeProgreso, Integer semestreEstimadoGraduacion,
    List<ResumenTipologia> porTipologia) {}