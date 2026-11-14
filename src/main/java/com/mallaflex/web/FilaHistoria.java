package com.mallaflex.web;

public record FilaHistoria(
    String nombre,
    String codigo,
    Integer creditos,
    Integer matricula,
    Double nota,
    String estado
) {}