package com.mallaflex.entity;

public record MateriaMalla(
    String codigo,
    String nombre,
    Integer creditos,
    String tipologia,
    Integer semestreSugerido,
    Boolean requiereMallaCompleta
) {}