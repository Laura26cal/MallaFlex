package com.mallaflex.model;

public record Usuario(
    Long id,
    String nombre,
    String correo,
    String password,
    String rol,
    String carreraCodigo
) {}