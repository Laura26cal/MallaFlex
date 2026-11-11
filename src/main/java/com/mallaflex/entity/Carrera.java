package com.mallaflex.entity;

public record Carrera(
    String codigo,
    String nombre,
    String plan,
    Integer creditosTotales,
    Integer numSemestres,
    Integer creditosLibreEleccion
) {}