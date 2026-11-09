package com.mallaflex.service;

import java.util.List;

import com.mallaflex.model.Carrera;
import com.mallaflex.model.Materia;

/**
 * Operaciones de administración del catálogo (FR-03 / US-04).
 * Solo debe usarlas el rol ADMIN; la restricción por rol se hace en la capa web.
 *
 * Todas las operaciones lanzan:
 *  - RecursoNoEncontradoException si una carrera o materia referida no existe.
 *  - ReglaNegocioException si se viola una regla (el mensaje se puede mostrar al usuario).
 */
public interface ICatalogoAdmin {

    // ---------- Carreras ----------

    Carrera crearCarrera(Carrera carrera);

    Carrera actualizarCarrera(Carrera carrera);

    void eliminarCarrera(String codigo);

    // ---------- Materias ----------

    /** Crea la materia con sus prerrequisitos y correquisitos (listas vacías o nulas si no tiene). */
    Materia crearMateria(Materia materia, List<String> prerrequisitos, List<String> correquisitos);

    /** Actualiza nombre y créditos. El código no cambia. */
    Materia actualizarMateria(Materia materia);

    void eliminarMateria(String codigo);

    void reemplazarPrerrequisitos(String materiaCodigo, List<String> prerrequisitos);

    void reemplazarCorrequisitos(String materiaCodigo, List<String> correquisitos);

    void reemplazarOpciones(String materiaCodigo, List<String> opciones);

    // ---------- Malla ----------

    /** Agrega la materia a la malla de la carrera. {@code semestreSugerido} puede ser null. */
    void asignarMateriaACarrera(String carreraCodigo, String materiaCodigo, String tipologia,
                                Integer semestreSugerido, boolean requiereMallaCompleta);

    void quitarMateriaDeCarrera(String carreraCodigo, String materiaCodigo);
}
