package com.mallaflex.model;

public record LibreEleccion(
    Long id,
    Long usuarioId,
    String nombre,
    Integer creditos,
    Integer numeroMatricula,
    Double nota,
    String estado
) {}