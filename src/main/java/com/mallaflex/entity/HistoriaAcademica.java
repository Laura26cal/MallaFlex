package com.mallaflex.model;

public record HistoriaAcademica(
    Long id,
    Long usuarioId,
    String materiaCodigo,
    Integer numeroMatricula,
    Double nota,
    String estado
) {}