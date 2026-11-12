package com.mallaflex.service;

import java.util.List;

import com.mallaflex.entity.Carrera;
import com.mallaflex.entity.Materia;

public interface ICatalogoAdmin {

    // carreras

    Carrera crearCarrera(Carrera carrera);

    Carrera actualizarCarrera(Carrera carrera);

    void eliminarCarrera(String codigo);

    // materias

    Materia crearMateria(Materia materia, List<String> prerrequisitos, List<String> correquisitos);

    Materia actualizarMateria(Materia materia);

    void eliminarMateria(String codigo);

    void reemplazarPrerrequisitos(String materiaCodigo, List<String> prerrequisitos);

    void reemplazarCorrequisitos(String materiaCodigo, List<String> correquisitos);

    void reemplazarOpciones(String materiaCodigo, List<String> opciones);

    // malla

    void asignarMateriaACarrera(String carreraCodigo, String materiaCodigo, String tipologia,
                                Integer semestreSugerido, boolean requiereMallaCompleta);

    void quitarMateriaDeCarrera(String carreraCodigo, String materiaCodigo);
}
