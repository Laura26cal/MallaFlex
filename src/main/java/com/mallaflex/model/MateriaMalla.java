package com.mallaflex.model;

public record MateriaMalla(
    String codigo,
    String nombre,
    Integer creditos,
    String tipologia,
    Integer semestreSugerido,
    Boolean requiereMallaCompleta
) {}